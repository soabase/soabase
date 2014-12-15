/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import io.soabase.client.SoaClientConfiguration;
import io.soabase.core.SoaConfiguration;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import io.soabase.zookeeper.discovery.ZooKeeperDiscoveryFactory;

public class ExampleConfiguration extends Configuration
{
    private SoaConfiguration soaConfiguration = new SoaConfiguration();
    private CuratorConfiguration curatorConfiguration = new CuratorConfiguration();
    private ZooKeeperDiscoveryFactory discoveryFactory = new ZooKeeperDiscoveryFactory();
    private SoaClientConfiguration clientConfiguration = new SoaClientConfiguration();
    private SqlConfiguration sqlConfiguration = new SqlConfiguration();

    public ExampleConfiguration()
    {
        clientConfiguration.setHttpClientConfiguration(new HttpClientConfiguration());
    }

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
}
