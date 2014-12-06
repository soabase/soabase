package io.soabase.client;

import com.google.common.collect.Lists;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("deprecation")
class WrappedHttpClient implements HttpClient
{
    private final HttpClient implementation;
    private final SoaDiscovery discovery;
    private final boolean retry500s;
    private final HttpRequestRetryHandler retryHandler;

    public WrappedHttpClient(HttpClient implementation, SoaDiscovery discovery, int retries, boolean retry500s)
    {
        this.implementation = implementation;
        this.discovery = discovery;
        this.retry500s = retry500s;
        retryHandler = new DefaultHttpRequestRetryHandler(retries, false, Lists.<Class<? extends IOException>>newArrayList()){};
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
        return new RetryLoop<HttpUriRequest>() {
            @Override
            protected HttpResponse execute(HttpUriRequest originalRequest, HttpContext context, SoaDiscoveryInstance instance) throws IOException
            {
                HttpUriRequest request = filterRequest(originalRequest, instance);
                return implementation.execute(request, context);
            }
        }.run(this, request, context, request.getURI().getHost());
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException
    {
        return execute(target, request, (HttpContext)null);
    }

    @Override
    public HttpResponse execute(HttpHost target, final HttpRequest request, HttpContext context) throws IOException
    {
        return new RetryLoop<HttpHost>() {
            @Override
            protected HttpResponse execute(HttpHost original, HttpContext context, SoaDiscoveryInstance instance) throws IOException
            {
                HttpHost target = filterTarget(original, instance);
                return implementation.execute(target, request, context);
            }
        }.run(this, target, context, target.getHostName());
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

    boolean isRetry500s()
    {
        return retry500s;
    }

    HttpRequestRetryHandler getRetryHandler()
    {
        return retryHandler;
    }

    private HttpHost filterTarget(HttpHost target, SoaDiscoveryInstance instance)
    {
        if ( instance != null )
        {
            String scheme = instance.isForceSsl() ? "https" : target.getSchemeName();
            target = new HttpHost(instance.getHost(), instance.getPort(), scheme);
        }
        return target;
    }

    private HttpUriRequest filterRequest(HttpUriRequest request, SoaDiscoveryInstance instance)
    {
        if ( instance != null )
        {
            try
            {
                URI uri = request.getURI();
                String scheme = instance.isForceSsl() ? "https" : uri.getScheme();
                uri = new URI(scheme, uri.getUserInfo(), instance.getHost(), instance.getPort(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
                request = new WrappedHttpUriRequest(request, uri);
            }
            catch ( URISyntaxException e )
            {
                throw new RuntimeException(e);
            }
        }
        return request;
    }
}
