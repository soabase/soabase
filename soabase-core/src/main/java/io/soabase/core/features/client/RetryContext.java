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
import io.soabase.core.features.discovery.DiscoveryInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;

class RetryContext
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final URI originalUri;
    private final String originalHost;
    private final String method;
    private final RetryComponents components;
    private volatile DiscoveryInstance instance;

    RetryContext(RetryComponents components, URI originalUri, String method)
    {
        this.components = Preconditions.checkNotNull(components, "components cannot be null");
        this.originalUri = Preconditions.checkNotNull(originalUri, "originalUri cannot be null");
        originalHost = Preconditions.checkNotNull(this.originalUri.getHost(), "request Host cannot be null");
        this.method = Preconditions.checkNotNull(method, "method cannot be null");
    }

    DiscoveryInstance getInstance()
    {
        return instance;
    }

    void setInstance(DiscoveryInstance instance)
    {
        this.instance = instance;
    }

    String getOriginalHost()
    {
        return originalHost;
    }

    URI getOriginalUri()
    {
        return originalUri;
    }

    RetryComponents getComponents()
    {
        return components;
    }

    boolean shouldBeRetried(int retryCount, int statusCode, Throwable exception)
    {
        if ( retryCount >= components.getMaxRetries() )
        {
            log.warn(String.format("Retries exceeded. retryCount: %d - maxRetries: %d", retryCount, components.getMaxRetries()));
            return false;
        }

        if ( (statusCode != 0) && components.isRetry500s() )
        {
            if ( (statusCode >= 500) && (statusCode <= 599) )
            {
                exception = new IOException("Internal Server Error: " + statusCode);
            }
        }
        boolean shouldBeRetried = shouldBeRetried(exception);
        if ( shouldBeRetried )
        {
            String serviceName = ClientUtils.hostToServiceName(originalHost);
            if ( (serviceName != null) && (instance != null) )
            {
                components.getDiscovery().noteError(serviceName, instance);
            }
        }
        return shouldBeRetried;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean shouldBeRetried(Throwable exception)
    {
        if ( exception == null )
        {
            return false;
        }

        boolean retry = false;
        if ( exception instanceof ConnectException )
        {
            retry = true;
        }
        else if ( isIdempotentMethod(method) )
        {
            retry = true;
        }

        if ( retry && (exception instanceof IOException) )
        {
            log.info(String.format("Retrying request due to exception %s. request: %s", exception.getClass().getSimpleName(), originalUri));
            return true;
        }

        return shouldBeRetried(exception.getCause());
    }

    private boolean isIdempotentMethod(String method)
    {
        return method.equalsIgnoreCase("get") || method.equalsIgnoreCase("put");
    }
}
