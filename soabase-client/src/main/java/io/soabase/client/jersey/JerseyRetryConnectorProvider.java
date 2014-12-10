package io.soabase.client.jersey;

import io.soabase.client.retry.RetryComponents;
import io.soabase.core.features.discovery.SoaDiscovery;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

public class JerseyRetryConnectorProvider implements ConnectorProvider
{
    private final SoaDiscovery discovery;
    private final RetryComponents retryComponents;

    public JerseyRetryConnectorProvider(SoaDiscovery discovery, RetryComponents retryComponents)
    {
        this.discovery = discovery;
        this.retryComponents = retryComponents;
    }

    @Override
    public Connector getConnector(Client client, Configuration runtimeConfig)
    {
        return new JerseyRetryConnector(client, retryComponents, runtimeConfig);
    }
}
