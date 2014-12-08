package io.soabase.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.soabase.client.SoaClientConfiguration;
import io.soabase.core.SoaConfiguration;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import io.soabase.zookeeper.discovery.ZooKeeperDiscoveryFactory;

public class ExampleConfiguration extends Configuration
{
    private SoaConfiguration soaConfiguration = new SoaConfiguration();
    private CuratorConfiguration curatorConfiguration = new CuratorConfiguration();
    private ZooKeeperDiscoveryFactory discoveryFactory = new ZooKeeperDiscoveryFactory();
    private SoaClientConfiguration clientConfiguration = new SoaClientConfiguration();

    @JsonProperty("soa")
    public SoaConfiguration getSoaConfiguration()
    {
        return soaConfiguration;
    }

    @JsonProperty("soa")
    public void setSoaConfiguration(SoaConfiguration soaConfiguration)
    {
        this.soaConfiguration = soaConfiguration;
    }

    @JsonProperty("curator")
    public CuratorConfiguration getCuratorConfiguration()
    {
        return curatorConfiguration;
    }

    @JsonProperty("curator")
    public void setCuratorConfiguration(CuratorConfiguration curatorConfiguration)
    {
        this.curatorConfiguration = curatorConfiguration;
    }

    @JsonProperty("discovery")
    public ZooKeeperDiscoveryFactory getDiscoveryFactory()
    {
        return discoveryFactory;
    }

    @JsonProperty("discovery")
    public void setDiscoveryFactory(ZooKeeperDiscoveryFactory discoveryFactory)
    {
        this.discoveryFactory = discoveryFactory;
    }

    @JsonProperty("client")
    public SoaClientConfiguration getClientConfiguration()
    {
        return clientConfiguration;
    }

    @JsonProperty("client")
    public void setClientConfiguration(SoaClientConfiguration clientConfiguration)
    {
        this.clientConfiguration = clientConfiguration;
    }
}
