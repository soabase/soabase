package io.soabase.core.features.client;

import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import java.net.URI;

public class RequestRunner<T>
{
    private final SoaRequestId.HeaderSetter<? super T> headerSetter;
    private final RetryContext retryContext;
    private int retryCount = 0;

    public RequestRunner(RetryComponents retryComponents, SoaRequestId.HeaderSetter<? super T> headerSetter, URI originalUri, String method)
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
        SoaRequestId.checkSetHeaders(request, headerSetter);
        SoaDiscoveryInstance instance = ClientUtils.hostToInstance(retryContext.getComponents().getDiscovery(), retryContext.getOriginalHost());
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
}
