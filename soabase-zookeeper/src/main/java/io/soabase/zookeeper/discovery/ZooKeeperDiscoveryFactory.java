package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import org.apache.curator.framework.CuratorFramework;
import javax.validation.Valid;

@JsonTypeName("zookeeper")
public class ZooKeeperDiscoveryFactory implements SoaDiscoveryFactory
{
    @Valid
    private String bindAddress;

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
        CuratorFramework curatorFramework = configuration.getNamedRequired(CuratorFramework.class, SoaFeatures.DEFAULT_NAME);
        return new ZooKeeperDiscovery(curatorFramework, mainPort, this, configuration.getThisServiceName());
    }
}
