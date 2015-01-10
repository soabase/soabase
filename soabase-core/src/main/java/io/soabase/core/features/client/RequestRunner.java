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

import io.soabase.core.features.discovery.DiscoveryInstance;
import java.net.URI;

public class RequestRunner<T>
{
    private final RequestId.HeaderSetter<? super T> headerSetter;
    private final RetryContext retryContext;
    private int retryCount = 0;

    public RequestRunner(RetryComponents retryComponents, RequestId.HeaderSetter<? super T> headerSetter, URI originalUri, String method)
    {
        this.headerSetter = headerSetter;
        retryContext = new RetryContext(retryComponents, originalUri, method);
    }

    public URI getOriginalUri()
    {
        return retryContext.getOriginalUri();
    }

    public URI prepareRequest(T request)
    {
        RequestId.checkSetHeaders(request, headerSetter);
        DiscoveryInstance instance = getDiscoveryInstance();
        retryContext.setInstance(instance);
        URI filteredUri = ClientUtils.filterUri(retryContext.getOriginalUri(), instance);
        return (filteredUri != null) ? filteredUri : retryContext.getOriginalUri();
    }

    public boolean shouldContinue()
    {
        return (retryCount < retryContext.getComponents().getMaxRetries());
    }

    public boolean isSuccessResponse(int statusCode)
    {
        return !retryContext.shouldBeRetried(retryCount++, statusCode, null);
    }

    public boolean shouldBeRetried(Throwable exception)
    {
        return retryContext.shouldBeRetried(retryCount++, 0, exception);
    }

    protected DiscoveryInstance getDiscoveryInstance()
    {
        return ClientUtils.hostToInstance(retryContext.getComponents().getDiscovery(), retryContext.getOriginalHost());
    }
}
