package io.soabase.client.jersey;

import io.soabase.client.Common;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.client.spi.Connector;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import java.net.URI;
import java.util.concurrent.Future;

public class JerseyRetryConnector implements Connector
{
    private final Connector connector;
    private final SoaDiscovery discovery;

    public JerseyRetryConnector(SoaDiscovery discovery, Client client, Configuration runtimeConfig)
    {
        this.discovery = discovery;
        connector = new ApacheConnectorProvider().getConnector(client, runtimeConfig);
    }

    @Override
    public ClientResponse apply(ClientRequest request)
    {
        // TODO
        filterRequest(request);
        return connector.apply(request);
    }

    @Override
    public Future<?> apply(ClientRequest request, AsyncConnectorCallback callback)
    {
        // TODO
        filterRequest(request);
        return connector.apply(request, callback);
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

    private void filterRequest(ClientRequest request)
    {
        SoaDiscoveryInstance instance = Common.hostToInstance(discovery, request.getUri().getHost());
        URI filteredUri = Common.filterUri(request.getUri(), instance);
        if ( filteredUri != null )
        {
            request.setUri(filteredUri);
        }
    }

/*
    @VisibleForTesting
    protected ClientResponse internalApply(ClientRequest request, int tryCount)
    {
        ClientResponse clientResponse;
        try
        {
            clientResponse = connector.apply(request);
        }
        catch ( ProcessingException e )
        {
            if ( tryRetry(request, tryCount, null, e) )
            {
                return internalApply(request, tryCount + 1);
            }
            throw e;
        }

        if ( retry.getRetryPolicy().shouldBeRetried(request.getUri(), tryCount, clientResponse, null, getRetryMode(request)) )
        {
            if ( tryRetry(request, tryCount, clientResponse, null) )
            {
                return internalApply(request, tryCount + 1);
            }
        }

        return clientResponse;
    }
*/
}
