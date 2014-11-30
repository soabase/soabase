package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;

@JsonTypeName("zookeeper")
public class ZooKeeperDiscoveryFactory implements SoaDiscoveryFactory
{
    @Override
    public SoaDiscovery build(Environment environment)
    {
        return new ZooKeeperDiscovery();
    }
}
