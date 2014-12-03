package io.soabase.core.features.client;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.discovery.SoaDiscovery;
import javax.ws.rs.client.Client;

@JsonTypeName("default")
public class StandardRestClientFactory implements SoaRestClientFactory
{
    @Override
    public Client build(SoaDiscovery discovery, Environment environment)
    {
        return null;    // TODO
    }
}
