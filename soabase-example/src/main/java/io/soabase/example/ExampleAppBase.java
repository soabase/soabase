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

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.client.SoaClientBundle;
import io.soabase.client.SoaClientConfiguration;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaConfiguration;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorBundle;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import org.apache.curator.test.InstanceSpec;
import java.io.Closeable;
import java.net.URL;
import java.util.List;

public abstract class ExampleAppBase extends Application<ExampleConfiguration> implements Managed
{
    private final List<Closeable> closeables = Lists.newArrayList();
    private final String configFqpn;

    public ExampleAppBase(String configFqpn)
    {
        this.configFqpn = configFqpn;
    }

    public void initialize(Bootstrap<ExampleConfiguration> bootstrap)
    {
        ConfigurationAccessor<ExampleConfiguration, SoaConfiguration> soaAccessor = new ConfigurationAccessor<ExampleConfiguration, SoaConfiguration>()
        {
            @Override
            public SoaConfiguration accessConfiguration(ExampleConfiguration configuration)
            {
                return configuration.getSoaConfiguration();
            }
        };
        ConfigurationAccessor<ExampleConfiguration, CuratorConfiguration> curatorAccessor = new ConfigurationAccessor<ExampleConfiguration, CuratorConfiguration>()
        {
            @Override
            public CuratorConfiguration accessConfiguration(ExampleConfiguration configuration)
            {
                return configuration.getCuratorConfiguration();
            }
        };
        ConfigurationAccessor<ExampleConfiguration, SoaClientConfiguration> clientAccessor = new ConfigurationAccessor<ExampleConfiguration, SoaClientConfiguration>()
        {
            @Override
            public SoaClientConfiguration accessConfiguration(ExampleConfiguration configuration)
            {
                return configuration.getClientConfiguration();
            }
        };
        ConfigurationAccessor<ExampleConfiguration, SqlConfiguration> sqlAccessor = new ConfigurationAccessor<ExampleConfiguration, SqlConfiguration>()
        {
            @Override
            public SqlConfiguration accessConfiguration(ExampleConfiguration configuration)
            {
                return configuration.getSqlConfiguration();
            }
        };
        bootstrap.addBundle(new CuratorBundle<>(soaAccessor, curatorAccessor));
        bootstrap.addBundle(new SqlBundle<>(soaAccessor, sqlAccessor));
        bootstrap.addBundle(new SoaBundle<>(soaAccessor));
        bootstrap.addBundle(new SoaClientBundle<>(soaAccessor, clientAccessor));
    }

    @Override
    public void run(String... arguments) throws Exception
    {
        if ( arguments.length == 0 )
        {
            URL config = Resources.getResource(configFqpn);

            System.setProperty("dw.curator.connectionString", "localhost:2181");
            System.setProperty("dw.soa.discovery.type", "zookeeper");
            System.setProperty("dw.soa.discovery.bindAddress", "localhost");
            System.setProperty("dw.sql.mybatisConfigUrl", "example-mybatis.xml");
            System.setProperty("dw.soa.attributes.type", "sql");
            System.setProperty("dw.server.applicationConnectors[0].port", "" + InstanceSpec.getRandomPort());
            System.setProperty("dw.server.adminConnectors[0].port", "" + InstanceSpec.getRandomPort());
            arguments = new String[]
            {
                "server",
                config.getPath()
            };
        }

        super.run(arguments);
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception
    {
        environment.lifecycle().manage(this);

        internalRun(configuration, environment);
    }

    protected abstract void internalRun(ExampleConfiguration configuration, Environment environment);

    @Override
    public void start() throws Exception
    {
        // NOP
    }

    @Override
    public void stop() throws Exception
    {
        for ( Closeable closeable : closeables )
        {
            closeable.close();
        }
    }
}
