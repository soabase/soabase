package io.soabase.core.features.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.discovery.SoaDiscovery;
import javax.ws.rs.client.Client;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = StandardRestClientFactory.class)
public interface SoaRestClientFactory
{
    public Client build(SoaDiscovery discovery, Environment environment);
}
