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
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.lifecycle.Managed;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.HealthyState;
import io.soabase.core.features.discovery.DiscoveryInstance;
import io.soabase.core.features.discovery.ExtendedDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.InstanceFilter;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

// TODO
public class ZooKeeperDiscovery extends CacheLoader<String, ServiceProvider<Payload>> implements ExtendedDiscovery, Managed, RemovalListener<String, ServiceProvider<Payload>>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<Payload> discovery;
    private final LoadingCache<String, ServiceProvider<Payload>> providers;
    private final AtomicReference<ServiceInstance<Payload>> us = new AtomicReference<>();
    private final String bindAddress;
    private final SoaInfo soaInfo;

    private static class FoundInstance
    {
        final ServiceInstance<Payload> instance;
        final ServiceProvider<Payload> provider;

        FoundInstance(ServiceInstance<Payload> instance, ServiceProvider<Payload> provider)
        {
            this.instance = instance;
            this.provider = provider;
        }
    }

    public ZooKeeperDiscovery(CuratorFramework curator, ZooKeeperDiscoveryFactory factory, SoaInfo soaInfo)
    {
        this.soaInfo = soaInfo;
        bindAddress = factory.getBindAddress();
        providers = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)  // TODO config
            .removalListener(this)
            .build(this);

        try
        {
            Payload payload = new Payload(soaInfo.getAdminPort(), Maps.<String, String>newHashMap(), ForcedState.CLEARED, HealthyState.UNHEALTHY);  // initially unhealthy

            us.set(buildInstance(payload, null));

            discovery = ServiceDiscoveryBuilder
                .builder(Payload.class)
                .basePath(factory.getZookeeperPath())
                .client(curator)
                .watchInstances(true)
                .build();
        }
        catch ( Exception e )
        {
            log.error("Could not build discovery instance", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<String> getServiceNames()
    {
        return providers.asMap().keySet();
    }

    @Override
    public Collection<String> queryForServiceNames()
    {
        try
        {
            // TODO - possibly cache this
            return discovery.queryForNames();
        }
        catch ( Exception e )
        {
            log.error("Could not query for names", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setHealthyState(HealthyState newHealthyState)
    {
        Payload payload = us.get().getPayload();
        updateRegistration(new Payload(payload.getAdminPort(), payload.getMetaData(), payload.getForcedState(), newHealthyState));
    }

    @Override
    public void setMetaData(Map<String, String> newMetaData)
    {
        Payload payload = us.get().getPayload();
        updateRegistration(new Payload(payload.getAdminPort(), newMetaData, payload.getForcedState(), payload.getHealthyState()));
    }

    @Override
    public void setForcedState(String serviceName, String instanceId, ForcedState forcedState)
    {
        try
        {
            ServiceInstance<Payload> foundInstance = discovery.queryForInstance(serviceName, instanceId);
            if ( foundInstance != null )
            {
                DiscoveryInstance soaInstance = toSoaInstance(foundInstance);
                Payload oldPayload = foundInstance.getPayload();
                Payload newPayload = new Payload(oldPayload.getAdminPort(), oldPayload.getMetaData(), forcedState, oldPayload.getHealthyState());
                ServiceInstance<Payload> updatedInstance = buildInstance(serviceName, soaInstance.getPort(), newPayload, instanceId, soaInstance.getHost());
                discovery.updateService(updatedInstance);
            } // TODO else?
        }
        catch ( Exception e )
        {
            log.error("Could not update service: " + (serviceName + ":" + instanceId), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServiceProvider<Payload> load(String serviceName) throws Exception
    {
        InstanceFilter<Payload> filter = new InstanceFilter<Payload>()
        {
            @Override
            public boolean apply(ServiceInstance<Payload> instance)
            {
                Payload payload = instance.getPayload();
                if ( payload.getForcedState() == ForcedState.CLEARED )
                {
                    return (payload.getHealthyState() == HealthyState.HEALTHY);
                }
                return (payload.getForcedState() == ForcedState.REGISTER);
            }
        };
        ServiceProvider<Payload> provider = discovery
            .serviceProviderBuilder()
            .serviceName(serviceName)
            .additionalFilter(filter)
            .build();
        provider.start();
        return provider;
    }

    @Override
    public void onRemoval(RemovalNotification<String, ServiceProvider<Payload>> notification)
    {
        CloseableUtils.closeQuietly(notification.getValue());
    }

    @Override
    public Collection<DiscoveryInstance> queryForAllInstances(String serviceName)
    {
        try
        {
            Collection<ServiceInstance<Payload>> serviceInstances = discovery.queryForInstances(serviceName);
            Iterable<DiscoveryInstance> transformed = Iterables.transform(serviceInstances, new Function<ServiceInstance<Payload>, DiscoveryInstance>()
            {
                @Nullable
                @Override
                public DiscoveryInstance apply(ServiceInstance<Payload> instance)
                {
                    return toSoaInstance(instance);
                }
            });
            return Lists.newArrayList(transformed);
        }
        catch ( Exception e )
        {
            log.error("Could query all instances for service: " + serviceName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<DiscoveryInstance> getAllInstances(String serviceName)
    {
        try
        {
            // TODO - validate service name
            ServiceProvider<Payload> provider = providers.get(serviceName);
            Collection<ServiceInstance<Payload>> allInstances = provider.getAllInstances();
            return Collections2.transform(allInstances, new Function<ServiceInstance<Payload>, DiscoveryInstance>()
            {
                @Nullable
                @Override
                public DiscoveryInstance apply(@Nullable ServiceInstance<Payload> instance)
                {
                    return toSoaInstance(instance);
                }
            });
        }
        catch ( Exception e )
        {
            log.error("Could not get service: " + serviceName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public DiscoveryInstance getInstance(String serviceName)
    {
        ServiceInstance<Payload> instance;
        try
        {
            // TODO - validate service name
            ServiceProvider<Payload> provider = providers.get(serviceName);
            instance = provider.getInstance();
            return toSoaInstance(instance);
        }
        catch ( Exception e )
        {
            log.error("Could not service instance: " + serviceName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void noteError(String serviceName, final DiscoveryInstance errorInstance)
    {
        FoundInstance foundInstance = findInstanceFromProvider(serviceName, errorInstance);
        if ( foundInstance != null )
        {
            foundInstance.provider.noteError(foundInstance.instance);
        }
    }

    @Override
    public void start() throws Exception
    {
        discovery.start();
        if ( soaInfo.isRegisterInDiscovery() )
        {
            discovery.registerService(us.get());
        }
    }

    @Override
    public void stop() throws Exception
    {
        providers.invalidateAll();
        CloseableUtils.closeQuietly(discovery);
    }

    private FoundInstance findInstanceFromProvider(String serviceName, final DiscoveryInstance instanceToFind)
    {
        ServiceInstance<Payload> foundInstance = null;
        ServiceProvider<Payload> provider = providers.getUnchecked(serviceName);
        if ( provider != null )
        {
            try
            {
                foundInstance = Iterables.find
                    (
                        provider.getAllInstances(),
                        new Predicate<ServiceInstance<Payload>>()
                        {
                            @Override
                            public boolean apply(ServiceInstance<Payload> instance)
                            {
                                return instanceToFind.getId().equals(instance.getId());
                            }
                        },
                        null
                    );
            }
            catch ( Exception e )
            {
                log.error("Could not find service: " + (serviceName + ":" + instanceToFind.getId()), e);
                throw new RuntimeException(e);
            }
        }
        return (foundInstance != null) ? new FoundInstance(foundInstance, provider) : null;
    }

    private DiscoveryInstance toSoaInstance(ServiceInstance<Payload> instance)
    {
        if ( instance == null )
        {
            return null;
        }

        Payload payload = instance.getPayload();
        int port = Objects.firstNonNull(instance.getPort(), Objects.firstNonNull(instance.getSslPort(), 0));
        return new DiscoveryInstanceImpl(instance.getId(), instance.getAddress(), port, instance.getSslPort() != null, payload);
    }

    private void updateRegistration(Payload newPayload)
    {
        if ( !soaInfo.isRegisterInDiscovery() )
        {
            return;
        }

        ServiceInstance<Payload> localUs = us.get();
        Payload currentPayload = localUs.getPayload();
        if ( !newPayload.equals(currentPayload) )
        {
            try
            {
                ServiceInstance<Payload> updatedInstance = buildInstance(newPayload, localUs.getId());
                us.set(updatedInstance);
                discovery.updateService(updatedInstance);
            }
            catch ( Exception e )
            {
                log.error("Could not update registration for local instance: " + localUs, e);
                throw new RuntimeException(e);
            }
        }
    }

    private ServiceInstance<Payload> buildInstance(Payload payload, String id) throws Exception
    {
        return buildInstance(soaInfo.getServiceName(), soaInfo.getMainPort(), payload, id, null);
    }

    private ServiceInstance<Payload> buildInstance(String serviceName, int mainPort, Payload payload, String id, String address) throws Exception
    {
        ServiceInstanceBuilder<Payload> builder = ServiceInstance.<Payload>builder()
            .name(serviceName)
            .payload(payload)
            .port(mainPort);
        if ( id != null )
        {
            builder = builder.id(id);
        }
        if ( address != null )
        {
            builder = builder.address(address);
        }
        else if ( bindAddress != null )
        {
            builder = builder.address(bindAddress);
        }
        return builder.build();
    }
}
