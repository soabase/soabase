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
package io.soabase.client.jersey;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SettableFuture;
import io.soabase.core.features.client.RequestRunner;
import io.soabase.core.features.client.RetryComponents;
import io.soabase.core.features.client.SoaRequestId;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.client.spi.Connector;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import java.net.URI;
import java.util.concurrent.Future;

public class JerseyRetryConnector implements Connector
{
    private final Connector connector;
    private final RetryComponents retryComponents;
    private final SoaRequestId.HeaderSetter<ClientRequest> headerSetter = new SoaRequestId.HeaderSetter<ClientRequest>()
    {
        @Override
        public void setHeader(ClientRequest request, String header, String value)
        {
            request.getHeaders().putSingle(header, value);
        }
    };

    public JerseyRetryConnector(Client client, RetryComponents retryComponents, Configuration runtimeConfig)
    {
        this.retryComponents = retryComponents;
        connector = new ApacheConnectorProvider().getConnector(client, runtimeConfig);
    }

    @Override
    public ClientResponse apply(ClientRequest request)
    {
        RequestRunner<ClientRequest> requestRunner = new RequestRunner<>(retryComponents, headerSetter, request.getUri(), request.getMethod());
        while ( requestRunner.shouldContinue() )
        {
            URI uri = requestRunner.prepareRequest(request);
            request.setUri(uri);
            try
            {
                ClientResponse response = connector.apply(request);
                if ( requestRunner.isSuccessResponse(response.getStatus()) )
                {
                    return response;
                }
            }
            catch ( Exception e )
            {
                if ( !requestRunner.shouldBeRetried(e) )
                {
                    throw new ProcessingException(e);
                }
            }
        }
        throw new ProcessingException("Retries expired: " + requestRunner.getOriginalUri());
    }

    @Override
    public Future<?> apply(final ClientRequest request, final AsyncConnectorCallback callback)
    {
        Preconditions.checkNotNull(callback, "callback is assumed to be non null");
        final RequestRunner<ClientRequest> requestRunner = new RequestRunner<>(retryComponents, headerSetter, request.getUri(), request.getMethod());
        AsyncConnectorCallback localCallback = new AsyncConnectorCallback()
        {
            @Override
            public void response(ClientResponse response)
            {
                if ( requestRunner.isSuccessResponse(response.getStatus()) )
                {
                    callback.response(response);
                }
                else
                {
                    asyncRetry(request, requestRunner, this);
                }
            }

            @Override
            public void failure(Throwable failure)
            {
                if ( requestRunner.shouldBeRetried(failure) )
                {
                    asyncRetry(request, requestRunner, this);
                }
                else
                {
                    callback.failure(failure);
                }
            }
        };

        request.setUri(requestRunner.prepareRequest(request));
        connector.apply(request, localCallback);
        return SettableFuture.create(); // just a dummy
    }

    @Override
    public String getName()
    {
        return connector.getName();
    }

    @Override
    public void close()
    {
        connector.close();
    }

    private void asyncRetry(final ClientRequest request, final RequestRunner<ClientRequest> requestRunner, final AsyncConnectorCallback callback)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                request.setUri(requestRunner.prepareRequest(request));
                connector.apply(request, callback);
            }
        };
        retryComponents.getExecutorService().submit(runnable);
    }
}
