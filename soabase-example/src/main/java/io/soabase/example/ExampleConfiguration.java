package io.soabase.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import io.soabase.core.SoaConfiguration;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.sql.attributes.SqlDynamicAttributesFactory;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import io.soabase.zookeeper.discovery.ZooKeeperDiscoveryFactory;

public class ExampleConfiguration extends Configuration
{
    private SoaConfiguration soaConfiguration = new SoaConfiguration();
    private SqlConfiguration sqlConfiguration = new SqlConfiguration();
    private CuratorConfiguration curatorConfiguration = new CuratorConfiguration();
    private SqlDynamicAttributesFactory attributesFactory = new SqlDynamicAttributesFactory();
    private ZooKeeperDiscoveryFactory discoveryFactory = new ZooKeeperDiscoveryFactory();
    private HttpClientConfiguration clientConfiguration = new HttpClientConfiguration();

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

    @JsonProperty("sql")
    public SqlConfiguration getSqlConfiguration()
    {
        return sqlConfiguration;
    }

    @JsonProperty("sql")
    public void setSqlConfiguration(SqlConfiguration sqlConfiguration)
    {
        this.sqlConfiguration = sqlConfiguration;
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

    @JsonProperty("attributes")
    public SqlDynamicAttributesFactory getAttributesFactory()
    {
        return attributesFactory;
    }

    @JsonProperty("attributes")
    public void setAttributesFactory(SqlDynamicAttributesFactory attributesFactory)
    {
        this.attributesFactory = attributesFactory;
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
    public HttpClientConfiguration getClientConfiguration()
    {
        return clientConfiguration;
    }

    @JsonProperty("client")
    public void setClientConfiguration(HttpClientConfiguration clientConfiguration)
    {
        this.clientConfiguration = clientConfiguration;
    }
}
