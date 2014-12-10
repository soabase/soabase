package io.soabase.client.apache;

import io.soabase.client.Common;
import io.soabase.client.retry.Retry;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("deprecation")
public class WrappedHttpClient implements HttpClient
{
    private final HttpClient implementation;
    private final SoaDiscovery discovery;
    private final Retry retry;

    public WrappedHttpClient(HttpClient implementation, SoaDiscovery discovery, Retry retry)
    {
        this.implementation = implementation;
        this.discovery = discovery;
        this.retry = retry;
    }

    @Override
    public HttpParams getParams()
    {
        return implementation.getParams();
    }

    @Override
    public ClientConnectionManager getConnectionManager()
    {
        return implementation.getConnectionManager();
    }

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException
    {
        return execute(request, (HttpContext)null);
    }

    @Override
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException
    {
        String originalHost = request.getURI().getHost();
        for ( int retryCount = 0; /* no check */; ++retryCount )
        {
            SoaDiscoveryInstance instance = Common.hostToInstance(discovery, originalHost);
            URI filteredUri = Common.filterUri(request.getURI(), instance);
            if ( filteredUri != null )
            {
                request = new WrappedHttpUriRequest(request, filteredUri);
            }
            try
            {
                HttpResponse response = implementation.execute(request, context);
                if ( !retry.shouldBeRetried(request.getURI(), request.getMethod(), retryCount, response.getStatusLine().getStatusCode(), null) )
                {
                    return response;
                }
            }
            catch ( IOException e )
            {
                if ( !retry.shouldBeRetried(request.getURI(), request.getMethod(), retryCount, 0, null) )
                {
                    throw e;
                }
            }
        }
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException
    {
        return execute(target, request, (HttpContext)null);
    }

    @Override
    public HttpResponse execute(HttpHost target, final HttpRequest request, HttpContext context) throws IOException
    {
        String originalHost = target.getHostName();
        for ( int retryCount = 0; /* no check */; ++retryCount )
        {
            URI uri;
            try
            {
                uri = new URI(target.toURI());
            }
            catch ( URISyntaxException e )
            {
                // TODO logging
                throw new IOException(e);
            }
            try
            {
                SoaDiscoveryInstance instance = Common.hostToInstance(discovery, originalHost);
                URI filteredUri = Common.filterUri(uri, instance);
                if ( filteredUri != null )
                {
                    target = new HttpHost(filteredUri.getHost(), filteredUri.getPort(), filteredUri.getScheme());
                }
                HttpResponse response = implementation.execute(target, request, context);
                if ( !retry.shouldBeRetried(uri, request.getRequestLine().getMethod(), retryCount, response.getStatusLine().getStatusCode(), null) )
                {
                    return response;
                }
            }
            catch ( IOException e )
            {
                if ( !retry.shouldBeRetried(uri, request.getRequestLine().getMethod(), retryCount, 0, null) )
                {
                    throw e;
                }
            }
        }
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException
    {
        // TODO
        return implementation.execute(request, responseHandler);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException
    {
        // TODO
        return implementation.execute(request, responseHandler, context);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException
    {
        // TODO
        return implementation.execute(target, request, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException
    {
        // TODO
        return implementation.execute(target, request, responseHandler, context);
    }

    SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    Retry getRetry()
    {
        return retry;
    }
}
