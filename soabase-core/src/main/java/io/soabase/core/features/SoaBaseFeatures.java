package io.soabase.core.features;

import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;

public class SoaBaseFeatures
{
    private final SoaDiscovery discovery;
    private final SoaDynamicAttributes attributes;
    private final String instanceName;
    private final String groupName;

    public SoaBaseFeatures(SoaDiscovery discovery, SoaDynamicAttributes attributes, String instanceName, String groupName)
    {
        this.discovery = discovery;
        this.attributes = attributes;
        this.instanceName = instanceName;
        this.groupName = groupName;
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

    public String getGroupName()
    {
        return groupName;
    }
}
