package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import org.apache.curator.framework.CuratorFramework;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@JsonTypeName("zookeeper")
public class ZooKeeperDiscoveryFactory implements SoaDiscoveryFactory
{
    @Valid
    @NotNull
    private String thisServiceName;

    @Valid
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Service Names can only contain letters and numbers")
    private String bindAddress;

    @JsonProperty("name")
    public String getThisServiceName()
    {
        return thisServiceName;
    }

    @JsonProperty("name")
    public void setThisServiceName(String thisServiceName)
    {
        this.thisServiceName = thisServiceName;
    }

    @JsonProperty("bindAddress")
    public String getBindAddress()
    {
        return bindAddress;
    }

    @JsonProperty("bindAddress")
    public void setBindAddress(String bindAddress)
    {
        this.bindAddress = bindAddress;
    }

    @Override
    public SoaDiscovery build(int mainPort, SoaConfiguration configuration, Environment environment)
    {
        CuratorFramework curatorFramework = Preconditions.checkNotNull(configuration.getNamed(CuratorFramework.class, SoaFeatures.DEFAULT_NAME), "CuratorBundle has not been added or initialized");
        return new ZooKeeperDiscovery(curatorFramework, mainPort, this);
    }
}
