package io.soabase.core.features;

import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import javax.ws.rs.client.Client;

public class SoaFeatures
{
    private final SoaDiscovery discovery;
    private final SoaDynamicAttributes attributes;
    private final String instanceName;
    private final Client restClient;

    public SoaFeatures(String instanceName, SoaDiscovery discovery, SoaDynamicAttributes attributes, Client restClient)
    {
        this.discovery = discovery;
        this.attributes = attributes;
        this.instanceName = instanceName;
        this.restClient = restClient;
    }

    public SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    public SoaDynamicAttributes getAttributes()
    {
        return attributes;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public Client getRestClient()
    {
        return restClient;
    }
}
