package io.soabase.client;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;

public class SoaRetryHandler implements HttpRequestRetryHandler
{
    public SoaRetryHandler(int retries)
    {
        // TODO
    }

    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext)
    {
        return false;
    }
}
