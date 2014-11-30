package io.soabase.core;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.SoaBaseFeatures;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.rest.DiscoveryApis;
import io.soabase.core.rest.DynamicAttributeApis;
import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SoaBaseApplication<T extends SoaBaseConfiguration> extends Application<T> implements Closeable
{
    private final AtomicReference<SoaBaseFeatures> features = new AtomicReference<SoaBaseFeatures>();
    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    private static final String DEFAULT_GROUP_NAME = "default";

    public SoaBaseFeatures getFeatures()
    {
        return features.get();
    }

    @Override
    public final void initialize(Bootstrap<T> bootstrap)
    {
        soaInitialize(bootstrap);
    }

    @Override
    public final void run(T configuration, Environment environment) throws Exception
    {
        environment.jersey().register(DiscoveryApis.class);
        environment.jersey().register(DynamicAttributeApis.class);

        updateInstanceName(configuration);

        SoaDiscovery discovery = configuration.getDiscoveryFactory().build(environment);
        SoaDynamicAttributes attributes = configuration.getAttributesFactory().build(environment, configuration.getGroupName(), configuration.getInstanceName());

        features.set(new SoaBaseFeatures(discovery, attributes, configuration.getInstanceName(), configuration.getGroupName()));

        soaRun(configuration, environment);
    }

    @Override
    public final void close()
    {
        if ( !isOpen.compareAndSet(false, true) )
        {
            return;
        }

        soaClose();
    }

    protected abstract void soaClose();

    protected abstract void soaRun(T configuration, Environment environment);

    protected abstract void soaInitialize(Bootstrap<T> bootstrap);

    private void updateInstanceName(T configuration) throws UnknownHostException
    {
        if ( configuration.getInstanceName() == null )
        {
            configuration.setInstanceName(InetAddress.getLocalHost().getHostName());
        }
        if ( configuration.getGroupName() == null )
        {
            configuration.setGroupName(DEFAULT_GROUP_NAME);
        }
    }
}
