package io.soabase.core.features;

import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;

public class SoaFeatures
{
    private final SoaDiscovery discovery;
    private final SoaDynamicAttributes attributes;
    private final String instanceName;

    public SoaFeatures(String instanceName, SoaDiscovery discovery, SoaDynamicAttributes attributes)
    {
        this.discovery = discovery;
        this.attributes = attributes;
        this.instanceName = instanceName;
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
}
