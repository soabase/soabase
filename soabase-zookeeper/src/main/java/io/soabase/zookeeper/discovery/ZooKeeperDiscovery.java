/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.zookeeper.discovery;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.lifecycle.Managed;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceProvider;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// TODO
public class ZooKeeperDiscovery extends CacheLoader<String, ServiceProvider<Payload>> implements SoaDiscovery, Managed, RemovalListener<String, ServiceProvider<Payload>>
{
    private final ServiceDiscovery<Payload> discovery;
    private final LoadingCache<String, ServiceProvider<Payload>> providers;
    private final ServiceInstance<Payload> us;
    private final AtomicReference<HealthyState> healthyState = new AtomicReference<>(HealthyState.HEALTHY);
    private final AtomicReference<ForcedState> forcedState = new AtomicReference<>(ForcedState.CLEARED);
    private final AtomicBoolean isRegistered = new AtomicBoolean(false);
    private final boolean registerInDiscovery;

    public ZooKeeperDiscovery(CuratorFramework curator, ZooKeeperDiscoveryFactory factory, SoaInfo soaInfo)
    {
        registerInDiscovery = soaInfo.isRegisterInDiscovery();
        providers = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)  // TODO config
            .removalListener(this)
            .build(this);

        try
        {
            Payload payload = new Payload(soaInfo.getAdminPort(), Maps.<String, String>newHashMap());   // TODO metadata

            // TODO - metadata, etc.
            ServiceInstanceBuilder<Payload> builder = ServiceInstance.<Payload>builder()
                .name(soaInfo.getServiceName())
                .payload(payload)
                .port(soaInfo.getMainPort());
            if ( factory.getBindAddress() != null )
            {
                builder = builder.address(factory.getBindAddress());
            }
            us = builder.build();

            discovery = ServiceDiscoveryBuilder
                .builder(Payload.class)
                .basePath(factory.getZookeeperPath())
                .client(curator)
                .build();
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<String> getCurrentServiceNames()
    {
        try
        {
            return discovery.queryForNames();
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setHealthyState(HealthyState healthyState)
    {
        this.healthyState.set(healthyState);
        updateRegistration();
    }

    @Override
    public HealthyState getHealthyState()
    {
        return healthyState.get();
    }

    @Override
    public void setForcedState(ForcedState forcedState)
    {
        this.forcedState.set(forcedState);
        updateRegistration();
    }

    @Override
    public ForcedState getForcedState()
    {
        return forcedState.get();
    }

    @Override
    public ServiceProvider<Payload> load(String serviceName) throws Exception
    {
        // TODO - other values
        ServiceProvider<Payload> provider = discovery.serviceProviderBuilder().serviceName(serviceName).build();
        provider.start();
        return provider;
    }

    @Override
    public void onRemoval(RemovalNotification<String, ServiceProvider<Payload>> notification)
    {
        CloseableUtils.closeQuietly(notification.getValue());
    }

    @Override
    public Collection<SoaDiscoveryInstance> getAllInstances(String serviceName)
    {
        try
        {
            ServiceProvider<Payload> provider = providers.get(serviceName);
            Collection<ServiceInstance<Payload>> allInstances = provider.getAllInstances();
            Iterable<SoaDiscoveryInstance> transformed = Iterables.transform(allInstances, new Function<ServiceInstance<Payload>, SoaDiscoveryInstance>()
            {
                @Nullable
                @Override
                public SoaDiscoveryInstance apply(ServiceInstance<Payload> instance)
                {
                    return toSoaInstance(instance);
                }
            });
            return Lists.newArrayList(transformed);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    @Override
    public SoaDiscoveryInstance getInstance(String serviceName)
    {
        try
        {
            ServiceProvider<Payload> provider = providers.get(serviceName);
            ServiceInstance<Payload> instance = provider.getInstance();
            return toSoaInstance(instance);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    @Override
    public void noteError(String serviceName, final SoaDiscoveryInstance errorInstance)
    {
        ServiceProvider<Payload> provider = providers.getUnchecked(serviceName);
        if ( provider != null )
        {
            try
            {
                ServiceInstance<Payload> foundInstance = Iterables.find
                    (
                        provider.getAllInstances(),
                        new Predicate<ServiceInstance<Payload>>()
                        {
                            @Override
                            public boolean apply(ServiceInstance<Payload> instance)
                            {
                                if ( instance.getAddress().equals(errorInstance.getHost()) )
                                {
                                    //noinspection SimplifiableIfStatement
                                    if ( errorInstance.getPort() != 0 )
                                    {
                                        return errorInstance.isForceSsl() ? (errorInstance.getPort() == instance.getSslPort()) : (errorInstance.getPort() == instance.getPort());
                                    }
                                    return true;
                                }
                                return false;
                            }
                        },
                        null
                    );
                if ( foundInstance != null )
                {
                    provider.noteError(foundInstance);
                }
            }
            catch ( Exception e )
            {
                // TODO logging
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void start() throws Exception
    {
        discovery.start();
    }

    @Override
    public void stop() throws Exception
    {
        providers.invalidateAll();
        CloseableUtils.closeQuietly(discovery);
    }

    private SoaDiscoveryInstance toSoaInstance(ServiceInstance<Payload> instance)
    {
        Payload payload = instance.getPayload();
        // TODO check for null
        if ( instance.getPort() != null )
        {
            return new SoaDiscoveryInstance(instance.getAddress(), instance.getPort(), payload.getAdminPort(), false, payload.getMetaData());
        }
        if ( instance.getSslPort() != null )
        {
            return new SoaDiscoveryInstance(instance.getAddress(), instance.getSslPort(), payload.getAdminPort(), true, payload.getMetaData());
        }
        return new SoaDiscoveryInstance(instance.getAddress(), 0, payload.getAdminPort(), true, payload.getMetaData());
    }

    private void updateRegistration()
    {
        if ( !registerInDiscovery )
        {
            return;
        }

        boolean shouldBeRegistered;
        if ( forcedState.get() != ForcedState.CLEARED )
        {
            shouldBeRegistered = (forcedState.get() == ForcedState.REGISTER);
        }
        else
        {
            shouldBeRegistered = (healthyState.get() == HealthyState.HEALTHY);
        }
        if ( isRegistered.compareAndSet(!shouldBeRegistered, shouldBeRegistered) )
        {
            try
            {
                if ( shouldBeRegistered )
                {
                    discovery.registerService(us);
                }
                else
                {
                    discovery.unregisterService(us);
                }
            }
            catch ( Exception e )
            {
                // TODO logging
                throw new RuntimeException(e);
            }
        }
    }
}
