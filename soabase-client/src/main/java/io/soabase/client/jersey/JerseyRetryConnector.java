package io.soabase.client.jersey;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SettableFuture;
import io.soabase.client.Common;
import io.soabase.client.retry.Retry;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
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
    private final SoaDiscovery discovery;
    private final Retry retry;

    public JerseyRetryConnector(SoaDiscovery discovery, Client client, Retry retry, Configuration runtimeConfig)
    {
        this.discovery = discovery;
        this.retry = retry;
        connector = new ApacheConnectorProvider().getConnector(client, runtimeConfig);
    }

    @Override
    public ClientResponse apply(ClientRequest request)
    {
        return internalApply(request, request.getUri().getHost(), 0);
    }

    @Override
    public Future<?> apply(ClientRequest request, AsyncConnectorCallback callback)
    {
        Preconditions.checkNotNull(callback, "callback is assumed to be non null");
        return internalApply(request, request.getUri().getHost(), callback, 0);
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

    private void filterRequest(ClientRequest request, String originalHost)
    {
        SoaDiscoveryInstance instance = Common.hostToInstance(discovery, originalHost);
        URI filteredUri = Common.filterUri(request.getUri(), instance);
        if ( filteredUri != null )
        {
            request.setUri(filteredUri);
        }
    }

    @VisibleForTesting
    protected ClientResponse internalApply(ClientRequest request, String originalHost, int tryCount)
    {
        ClientResponse clientResponse;
        try
        {
            filterRequest(request, originalHost);
            clientResponse = connector.apply(request);
        }
        catch ( ProcessingException e )
        {
            if ( retry.shouldBeRetried(request.getUri(), request.getMethod(), tryCount, 0, e) )
            {
                return internalApply(request, originalHost, tryCount + 1);
            }
            throw e;
        }

        if ( retry.shouldBeRetried(request.getUri(), request.getMethod(), tryCount, clientResponse.getStatus(), null) )
        {
            return internalApply(request, originalHost, tryCount + 1);
        }

        return clientResponse;
    }


    @VisibleForTesting
    protected Future<?> internalApply(final ClientRequest request, final String originalHost, final AsyncConnectorCallback callback, final int tryCount)
    {
        AsyncConnectorCallback localCallback = new AsyncConnectorCallback()
        {
            @Override
            public void response(ClientResponse response)
            {
                if ( !isRetry(request, response, originalHost, null, callback, tryCount) )
                {
                    callback.response(response);
                }
            }

            @Override
            public void failure(Throwable failure)
            {
                if ( !isRetry(request, null, originalHost, failure, callback, tryCount) )
                {
                    callback.failure(failure);
                }
            }
        };
        filterRequest(request, originalHost);
        connector.apply(request, localCallback);
        return SettableFuture.create(); // just a dummy
    }

    private boolean isRetry(final ClientRequest request, ClientResponse response, final String originalHost, Throwable failure, final AsyncConnectorCallback callback, final int tryCount)
    {
        int status = (response != null) ? response.getStatus() : 0;
        if ( retry.shouldBeRetried(request.getUri(), request.getMethod(), tryCount, status, failure) )
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    internalApply(request, originalHost, callback, tryCount + 1);
                }
            };
            retry.getExecutorService().submit(runnable);
            return true;
        }
        return false;
    }
}
