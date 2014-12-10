package io.soabase.client.retry;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;

@JsonTypeName("default")
public class DefaultRetryHandler implements RetryHandler
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean shouldBeRetried(URI uri, String method, int retryCount, int maxRetries, int statusCode, Throwable exception, boolean retry500s)
    {
        if ( retryCount >= maxRetries )
        {
            log.warn(String.format("Retries exceeded. retryCount: %d - maxRetries: %d", retryCount, maxRetries));
            return false;
        }

        if ( (statusCode != 0) && retry500s )
        {
            if ( (statusCode >= 500) && (statusCode <= 599) )
            {
                exception = new IOException("Internal Server Error: " + statusCode);
            }
        }
        return shouldBeRetried(uri, exception, method);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean shouldBeRetried(URI uri, Throwable exception, String method)
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
            log.info(String.format("Retrying request due to exception %s. request: %s", exception.getClass().getSimpleName(), uri));
            return true;
        }

        return shouldBeRetried(uri, exception.getCause(), method);
    }

    private boolean isIdempotentMethod(String method)
    {
        return method.equalsIgnoreCase("get") || method.equalsIgnoreCase("put");
    }
}
