package io.soabase.client;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.client.spi.Connector;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import java.util.concurrent.Future;

public class JerseyRetryConnector implements Connector
{
    private final Connector connector;

    public JerseyRetryConnector(Client client, Configuration runtimeConfig)
    {
        connector = new ApacheConnectorProvider().getConnector(client, runtimeConfig);
    }

    @Override
    public ClientResponse apply(ClientRequest request)
    {
        // TODO
        return connector.apply(request);
    }

    @Override
    public Future<?> apply(ClientRequest request, AsyncConnectorCallback callback)
    {
        // TODO
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
}
