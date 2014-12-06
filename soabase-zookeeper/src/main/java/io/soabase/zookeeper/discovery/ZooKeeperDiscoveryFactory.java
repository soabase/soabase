package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import org.apache.curator.framework.CuratorFramework;

@JsonTypeName("zookeeper")
public class ZooKeeperDiscoveryFactory implements SoaDiscoveryFactory
{
    @Override
    public SoaDiscovery build(SoaConfiguration configuration, Environment environment)
    {
        CuratorFramework curatorFramework = Preconditions.checkNotNull(CuratorBundle.getCuratorFramework(configuration), "CuratorBundle has not been added or initialized");
        return new ZooKeeperDiscovery(curatorFramework, this);
    }
}
