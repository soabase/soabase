package io.soabase.client.retry;

import io.dropwizard.lifecycle.setup.ExecutorServiceBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class RetryExecutor
{
    private final ExecutorServiceBuilder builder;
    private final AtomicReference<ExecutorService> executorService = new AtomicReference<>();

    private volatile Accessor accessor = new Accessor() // a Thunk
    {
        @Override
        public ExecutorService getExecutorService()
        {
            synchronized(RetryExecutor.this)
            {
                if ( executorService.get() == null )
                {
                    executorService.set(builder.build());
                }
                accessor = new Accessor()
                {
                    @Override
                    public ExecutorService getExecutorService()
                    {
                        return executorService.get();
                    }
                };
                return executorService.get();
            }
        }
    };

    private interface Accessor
    {
        ExecutorService getExecutorService();
    }

    public RetryExecutor(ExecutorServiceBuilder builder)
    {
        this.builder = builder;
    }

    public ExecutorService getExecutorService()
    {
        return accessor.getExecutorService();
    }
}
