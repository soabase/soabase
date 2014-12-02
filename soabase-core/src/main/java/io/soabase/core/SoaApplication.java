package io.soabase.core;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.SoaFeatures;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.rest.DiscoveryApis;
import io.soabase.core.rest.DynamicAttributeApis;
import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SoaApplication<T extends SoaConfiguration> extends Application<T> implements Closeable
{
    private final AtomicReference<SoaFeatures> features = new AtomicReference<>();
    private final AtomicBoolean isOpen = new AtomicBoolean(true);
    private final AtomicReference<ExecutorService> service = new AtomicReference<>();

    public SoaFeatures getFeatures()
    {
        return features.get();
    }

    public ExecutorService getExecutorService()
    {
        return service.get();
    }

    @Override
    public final void initialize(Bootstrap<T> bootstrap)
    {
        soaInitialize(bootstrap);
    }

    public final void runAsync(final String[] args)
    {
        service.set(Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("SoaApplication-%d").build()));
        Callable<Void> callable = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                run(args);
                return null;
            }
        };
        service.get().submit(callable);
    }

    @Override
    public final void run(T configuration, Environment environment) throws Exception
    {
        environment.jersey().register(DiscoveryApis.class);
        environment.jersey().register(DynamicAttributeApis.class);

        updateInstanceName(configuration);
        List<String> scopes = Lists.newArrayList();
        scopes.add(configuration.getInstanceName());
        scopes.addAll(configuration.getScopes());

        SoaDiscovery discovery = checkManaged(environment, configuration.getDiscoveryFactory().build(environment));
        SoaDynamicAttributes attributes = checkManaged(environment, configuration.getAttributesFactory().build(environment, scopes));
        features.set(new SoaFeatures(discovery, attributes, configuration.getInstanceName()));

        soaRun(features.get(), configuration, environment);
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

    protected abstract void soaRun(SoaFeatures features, T configuration, Environment environment);

    protected abstract void soaInitialize(Bootstrap<T> bootstrap);

    private void updateInstanceName(T configuration) throws UnknownHostException
    {
        if ( configuration.getInstanceName() == null )
        {
            configuration.setInstanceName(InetAddress.getLocalHost().getHostName());
        }
    }

    private static <T> T checkManaged(Environment environment, T obj)
    {
        if ( obj instanceof Managed )
        {
            environment.lifecycle().manage((Managed)obj);
        }
        return obj;
    }
}
