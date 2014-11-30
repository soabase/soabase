package io.soabase.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.soabase.core.features.attributes.NullDynamicAttributesFactory;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import io.soabase.core.features.discovery.NullDiscoveryFactory;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

public class SoaBaseConfiguration extends Configuration
{
    @Valid
    @NotNull
    private SoaDiscoveryFactory discoveryFactory = new NullDiscoveryFactory();

    @Valid
    @NotNull
    private SoaDynamicAttributesFactory attributesFactory = new NullDynamicAttributesFactory();

    @Valid
    private int shutdownWaitMaxMs = (int)TimeUnit.SECONDS.toMillis(5);

    @Valid
    private String instanceName;

    @Valid
    private String groupName;

    @JsonProperty("discovery")
    public SoaDiscoveryFactory getDiscoveryFactory()
    {
        return discoveryFactory;
    }

    @JsonProperty("discovery")
    public void setDiscoveryFactory(SoaDiscoveryFactory discoveryFactory)
    {
        this.discoveryFactory = discoveryFactory;
    }

    @JsonProperty("attributes")
    public SoaDynamicAttributesFactory getAttributesFactory()
    {
        return attributesFactory;
    }

    @JsonProperty("attributes")
    public void setAttributesFactory(SoaDynamicAttributesFactory attributesFactory)
    {
        this.attributesFactory = attributesFactory;
    }

    @JsonProperty("shutdownWaitMaxMs")
    public int getShutdownWaitMaxMs()
    {
        return shutdownWaitMaxMs;
    }

    @JsonProperty("shutdownWaitMaxMs")
    public void setShutdownWaitMaxMs(int shutdownWaitMaxMs)
    {
        this.shutdownWaitMaxMs = shutdownWaitMaxMs;
    }

    @JsonProperty("instanceName")
    public String getInstanceName()
    {
        return instanceName;
    }

    @JsonProperty("instanceName")
    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }

    @JsonProperty("groupName")
    public String getGroupName()
    {
        return groupName;
    }

    @JsonProperty("groupName")
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    public String toString()
    {
        return "SoaBaseConfiguration{" +
            "discoveryFactory=" + discoveryFactory +
            ", attributesFactory=" + attributesFactory +
            ", shutdownWaitMaxMs=" + shutdownWaitMaxMs +
            ", instanceName='" + instanceName + '\'' +
            ", groupName='" + groupName + '\'' +
            "} " + super.toString();
    }
}
