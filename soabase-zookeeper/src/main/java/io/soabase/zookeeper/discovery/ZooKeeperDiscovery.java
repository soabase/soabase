package io.soabase.zookeeper.discovery;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Iterables;
import io.dropwizard.lifecycle.Managed;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceProvider;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// TODO
public class ZooKeeperDiscovery extends CacheLoader<String, ServiceProvider<Void>> implements SoaDiscovery, Managed, RemovalListener<String, ServiceProvider<Void>>
{
    private final ServiceDiscovery<Void> discovery;
    private final LoadingCache<String, ServiceProvider<Void>> providers;
    private final ServiceInstance<Void> us;
    private final AtomicReference<HealthyState> healthyState = new AtomicReference<>(HealthyState.HEALTHY);
    private final AtomicReference<ForcedState> forcedState = new AtomicReference<>(ForcedState.CLEARED);
    private final AtomicBoolean isRegistered = new AtomicBoolean(false);

    public ZooKeeperDiscovery(CuratorFramework curator, int mainPort, ZooKeeperDiscoveryFactory factory)
    {
        providers = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)  // TODO config
            .removalListener(this)
            .build(this);

        try
        {
            // TODO
            ServiceInstanceBuilder<Void> builder = ServiceInstance.<Void>builder()
                .name(factory.getThisServiceName())
                .port(mainPort);
            if ( factory.getBindAddress() != null )
            {
                builder = builder.address(factory.getBindAddress());
            }
            us = builder.build();

            discovery = ServiceDiscoveryBuilder
                .builder(Void.class)
                .basePath("/")  // TODO
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
    public void setHealthyState(HealthyState healthyState)
    {
        this.healthyState.set(healthyState);
        updateRegistration();
    }

    @Override
    public void setForcedState(ForcedState forcedState)
    {
        this.forcedState.set(forcedState);
        updateRegistration();
    }

    @Override
    public ServiceProvider<Void> load(String serviceName) throws Exception
    {
        // TODO - other values
        ServiceProvider<Void> provider = discovery.serviceProviderBuilder().serviceName(serviceName).build();
        provider.start();
        return provider;
    }

    @Override
    public void onRemoval(RemovalNotification<String, ServiceProvider<Void>> notification)
    {
        CloseableUtils.closeQuietly(notification.getValue());
    }

    @Override
    public Collection<SoaDiscoveryInstance> getAllInstances(String serviceName)
    {
        // TODO
        return null;
    }

    @Override
    public SoaDiscoveryInstance getInstance(String serviceName)
    {
        try
        {
            ServiceProvider<Void> provider = providers.get(serviceName);
            ServiceInstance<Void> instance = provider.getInstance();
            // TODO check for null
            if ( instance.getPort() != null )
            {
                return new SoaDiscoveryInstance(instance.getAddress(), instance.getPort(), false);
            }
            if ( instance.getSslPort() != null )
            {
                return new SoaDiscoveryInstance(instance.getAddress(), instance.getSslPort(), true);
            }
            return new SoaDiscoveryInstance(instance.getAddress(), 0, true);
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
        ServiceProvider<Void> provider = providers.getUnchecked(serviceName);
        if ( provider != null )
        {
            try
            {
                ServiceInstance<Void> foundInstance = Iterables.find
                    (
                        provider.getAllInstances(),
                        new Predicate<ServiceInstance<Void>>()
                        {
                            @Override
                            public boolean apply(ServiceInstance<Void> instance)
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

    private void updateRegistration()
    {
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
