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
