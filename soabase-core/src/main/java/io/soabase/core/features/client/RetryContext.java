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
package io.soabase.core.features.client;

import com.google.common.base.Preconditions;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import java.net.URI;

public class RetryContext
{
    private final URI originalUri;
    private final String originalHost;
    private final String method;
    private final RetryComponents components;
    private volatile SoaDiscoveryInstance instance;

    public RetryContext(RetryComponents components, URI originalUri, String method)
    {
        this.components = Preconditions.checkNotNull(components, "components cannot be null");
        this.originalUri = Preconditions.checkNotNull(originalUri, "originalUri cannot be null");
        originalHost = Preconditions.checkNotNull(this.originalUri.getHost(), "request Host cannot be null");
        this.method = Preconditions.checkNotNull(method, "method cannot be null");
    }

    public SoaDiscoveryInstance getInstance()
    {
        return instance;
    }

    public void setInstance(SoaDiscoveryInstance instance)
    {
        this.instance = instance;
    }

    public boolean shouldBeRetried(int retryCount, int statusCode, Throwable exception)
    {
        return components.getRetryHandler().shouldBeRetried(this, retryCount, statusCode, exception);
    }

    public String getOriginalHost()
    {
        return originalHost;
    }

    public URI getOriginalUri()
    {
        return originalUri;
    }

    public String getMethod()
    {
        return method;
    }

    public RetryComponents getComponents()
    {
        return components;
    }
}
