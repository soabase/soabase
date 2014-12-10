package io.soabase.client.retry;

import com.google.common.base.Preconditions;
import io.soabase.core.features.discovery.SoaDiscovery;
import java.util.concurrent.ExecutorService;

public class RetryComponents
{
    private final RetryHandler retryHandler;
    private final SoaDiscovery discovery;
    private final int retries;
    private final boolean retry500s;
    private final RetryExecutor retryExecutor;

    public RetryComponents(RetryHandler retryHandler, SoaDiscovery discovery, int retries, boolean retry500s, RetryExecutor retryExecutor)
    {
        this.retryHandler = Preconditions.checkNotNull(retryHandler, "retryHandler cannot be null");
        this.discovery = Preconditions.checkNotNull(discovery, "discovery cannot be null");
        this.retries = retries;
        this.retry500s = retry500s;
        this.retryExecutor = Preconditions.checkNotNull(retryExecutor, "retryExecutor cannot be null");
    }

    public ExecutorService getExecutorService()
    {
        return retryExecutor.getExecutorService();
    }

    public RetryHandler getRetryHandler()
    {
        return retryHandler;
    }

    public SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    public int getRetries()
    {
        return retries;
    }

    public boolean isRetry500s()
    {
        return retry500s;
    }
}
