package io.soabase.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;

abstract class RetryLoop<T>
{
    private final HttpRequestRetryHandler retryHandler;
    private final T original;
    private final HttpContext context;
    private final boolean retry500s;

    RetryLoop(HttpRequestRetryHandler retryHandler, T original, HttpContext context, boolean retry500s)
    {
        this.retryHandler = retryHandler;
        this.original = original;
        this.context = (context != null) ? context : new BasicHttpContext();
        this.retry500s = retry500s;
    }

    HttpResponse run() throws IOException
    {
        HttpResponse response = null;
        int count = 0;
        boolean done = false;
        while ( !done )
        {
            try
            {
                ++count;
                response = execute(original, context);
                if ( retry500s )
                {
                    int status = response.getStatusLine().getStatusCode();
                    if ( (status >= 500) && (status <= 599) )
                    {
                        throw new IOException("Bad status: " + status);
                    }
                }
                done = true;
            }
            catch ( IOException e )
            {
                if ( !retryHandler.retryRequest(e, count, context) )
                {
                    throw e;
                }
            }
        }
        return response;
    }

    protected abstract HttpResponse execute(T original, HttpContext context) throws IOException;
}
