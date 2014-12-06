package io.soabase.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SoaApplication<T extends SoaConfiguration> extends Application<T> implements Closeable
{
    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    @Override
    public final void initialize(Bootstrap<T> bootstrap)
    {
        bootstrap.addBundle(new SoaBundle<T>());
        soaInitialize(bootstrap);
    }

    public final void runAsync(final String[] args)
    {
        ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("SoaApplication-%d").build());
        Callable<Void> callable = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                run(args);
                return null;
            }
        };
        service.submit(callable);
    }

    @Override
    public final void run(T configuration, Environment environment) throws Exception
    {
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
}
