package io.soabase.client.retry;

import io.dropwizard.lifecycle.setup.ExecutorServiceBuilder;
import java.net.URI;
import java.util.concurrent.ExecutorService;

public class Retry
{
    private final RetryHandler retryHandler;
    private final int retries;
    private final boolean retry500s;
    private final RetryExecutor retryExecutor;

    public Retry(RetryHandler retryHandler, int retries, boolean retry500s, RetryExecutor retryExecutor)
    {
        this.retryHandler = retryHandler;
        this.retries = retries;
        this.retry500s = retry500s;
        this.retryExecutor = retryExecutor;
    }

    public ExecutorService getExecutorService()
    {
        return retryExecutor.getExecutorService();
    }

    public boolean shouldBeRetried(URI uri, String method, int retryCount, int statusCode, Throwable exception)
    {
        return retryHandler.shouldBeRetried(uri, method, retryCount, retries, statusCode, exception, retry500s);
    }
}
