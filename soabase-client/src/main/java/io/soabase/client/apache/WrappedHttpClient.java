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
package io.soabase.client.apache;

import io.soabase.core.features.client.RequestRunner;
import io.soabase.core.features.client.RetryComponents;
import io.soabase.core.features.client.RequestId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("deprecation")
public class WrappedHttpClient implements HttpClient
{
    private final HttpClient implementation;
    private final RetryComponents retryComponents;
    private final RequestId.HeaderSetter<HttpRequest> headerSetter = new RequestId.HeaderSetter<HttpRequest>()
    {
        @Override
        public void setHeader(HttpRequest request, String header, String value)
        {
            request.addHeader(header, value);
        }
    };

    public WrappedHttpClient(HttpClient implementation, RetryComponents retryComponents)
    {
        this.implementation = implementation;
        this.retryComponents = retryComponents;
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
        RequestRunner<HttpUriRequest> requestRunner = new RequestRunner<>(retryComponents, headerSetter, request.getURI(), request.getMethod());
        while ( requestRunner.shouldContinue() )
        {
            URI uri = requestRunner.prepareRequest(request);
            request = new WrappedHttpUriRequest(request, uri);
            try
            {
                HttpResponse response = implementation.execute(request, context);
                if ( requestRunner.isSuccessResponse(response.getStatusLine().getStatusCode()) )
                {
                    return response;
                }
            }
            catch ( IOException e )
            {
                if ( !requestRunner.shouldBeRetried(e) )
                {
                    throw e;
                }
            }
        }

        throw new IOException("Retries expired for " + requestRunner.getOriginalUri());
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException
    {
        return execute(target, request, (HttpContext)null);
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException
    {
        URI uri;
        try
        {
            uri = new URI(request.getRequestLine().getUri());
        }
        catch ( URISyntaxException e )
        {
            // TODO logging
            throw new IOException(e);
        }

        RequestRunner<HttpRequest> requestRunner = new RequestRunner<>(retryComponents, headerSetter, uri, request.getRequestLine().getMethod());
        while ( requestRunner.shouldContinue() )
        {
            uri = requestRunner.prepareRequest(request);
            request = new WrappedHttpRequest(request, uri);
            target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            try
            {
                HttpResponse response = implementation.execute(target, request, context);
                if ( requestRunner.isSuccessResponse(response.getStatusLine().getStatusCode()) )
                {
                    return response;
                }
            }
            catch ( IOException e )
            {
                if ( !requestRunner.shouldBeRetried(e) )
                {
                    throw e;
                }
            }
        }

        throw new IOException("Retries expired for " + requestRunner.getOriginalUri());
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException
    {
        return internalExecute(request, null, null, responseHandler, null);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException
    {
        return internalExecute(request, null, null, responseHandler, context);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException
    {
        return internalExecute(null, target, request, responseHandler, null);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException
    {
        return internalExecute(null, target, request, responseHandler, context);
    }

    // mostly copied from CloseableHttpClient.execute()
    private <T> T internalExecute(final HttpUriRequest uriRequest, final HttpHost target, final HttpRequest request,
                         final ResponseHandler<? extends T> responseHandler, final HttpContext context)
        throws IOException
    {
        Args.notNull(responseHandler, "Response handler");

        final HttpResponse response = (uriRequest != null) ? execute(uriRequest, context) : execute(target, request, context);

        final T result;
        try {
            result = responseHandler.handleResponse(response);
        } catch (final Exception t) {
            final HttpEntity entity = response.getEntity();
            try {
                EntityUtils.consume(entity);
            } catch (final Exception t2) {
                // Log this exception. The original exception is more
                // important and will be thrown to the caller.
                // TODO this.log.warn("Error consuming content after an exception.", t2);
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            //noinspection ConstantConditions
            if (t instanceof IOException) {
                throw (IOException) t;
            }
            throw new UndeclaredThrowableException(t);
        }

        // Handling the response was successful. Ensure that the content has
        // been fully consumed.
        final HttpEntity entity = response.getEntity();
        EntityUtils.consume(entity);
        return result;
    }
}
