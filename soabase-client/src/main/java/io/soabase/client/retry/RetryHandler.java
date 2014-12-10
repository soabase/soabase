package io.soabase.client.retry;

import org.glassfish.jersey.client.ClientResponse;
import java.net.URI;

public interface RetryHandler
{
    /**
     * Return true if the given arguments require a retry
     *
     * @param uri request URI
     * @param method the request type (GET, PUT, etc.)
     * @param retryCount 0 based retry count
     * @param maxRetries max retries
     * @param statusCode the response status code or 0
     * @param exception any exception (might be null)
     * @param retry500s if true, statuses in the 500 range will be retried
     * @return true/false
     */
    public boolean shouldBeRetried(URI uri, String method, int retryCount, int maxRetries, int statusCode, Throwable exception, boolean retry500s);
}
