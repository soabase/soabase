package io.soabase.client;

import com.google.common.base.Preconditions;
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

    public static final String HOST_SUBSTITUTION_TOKEN = "$";

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
        return new RetryLoop<HttpUriRequest>(retryHandler, request, context, retry500s) {
            @Override
            protected HttpResponse execute(HttpUriRequest originalRequest, HttpContext context) throws IOException
            {
                HttpUriRequest request = filterRequest(originalRequest);
                return implementation.execute(request, context);
            }
        }.run();
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException
    {
        return execute(target, request, (HttpContext)null);
    }

    @Override
    public HttpResponse execute(HttpHost target, final HttpRequest request, HttpContext context) throws IOException
    {
        return new RetryLoop<HttpHost>(retryHandler, target, context, retry500s) {
            @Override
            protected HttpResponse execute(HttpHost original, HttpContext context) throws IOException
            {
                HttpHost target = filterTarget(original);
                return implementation.execute(target, request, context);
            }
        }.run();
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException
    {
        request = filterRequest(request);
        return implementation.execute(request, responseHandler);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException
    {
        request = filterRequest(request);
        return implementation.execute(request, responseHandler, context);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException
    {
        target = filterTarget(target);
        return implementation.execute(target, request, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException
    {
        target = filterTarget(target);
        return implementation.execute(target, request, responseHandler, context);
    }

    private HttpHost filterTarget(HttpHost target)
    {
        String host = target.getHostName();
        SoaDiscoveryInstance instance = hostToInstance(host);
        if ( instance != null )
        {
            String scheme = instance.isForceSsl() ? "https" : target.getSchemeName();
            target = new HttpHost(instance.getHost(), instance.getPort(), scheme);
        }
        return target;
    }

    private HttpUriRequest filterRequest(HttpUriRequest request)
    {
        URI uri = request.getURI();
        String host = uri.getHost();
        SoaDiscoveryInstance instance = hostToInstance(host);
        if ( instance != null )
        {
            try
            {
                String scheme = instance.isForceSsl() ? "https" : uri.getScheme();
                uri = new URI(scheme, uri.getUserInfo(), instance.getHost(), instance.getPort(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
            }
            catch ( URISyntaxException e )
            {
                throw new RuntimeException(e);
            }
            request = new WrappedHttpUriRequest(request, uri);
        }
        return request;
    }

    private SoaDiscoveryInstance hostToInstance(String host)
    {
        if ( host.startsWith(HOST_SUBSTITUTION_TOKEN) && (host.length() > 1) )
        {
            String serviceName = host.substring(1);
            return Preconditions.checkNotNull(discovery.getInstance(serviceName), "No instance found for " + serviceName);
        }
        return null;
    }
}
