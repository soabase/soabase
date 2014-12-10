package io.soabase.client.jersey;

import io.soabase.client.retry.Retry;
import io.soabase.core.features.discovery.SoaDiscovery;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

public class JerseyRetryConnectorProvider implements ConnectorProvider
{
    private final SoaDiscovery discovery;
    private final Retry retry;

    public JerseyRetryConnectorProvider(SoaDiscovery discovery, Retry retry)
    {
        this.discovery = discovery;
        this.retry = retry;
    }

    @Override
    public Connector getConnector(Client client, Configuration runtimeConfig)
    {
        return new JerseyRetryConnector(discovery, client, retry, runtimeConfig);
    }
}
