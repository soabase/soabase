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
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.client.SoaClientBundle;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaInfo;
import io.soabase.core.config.FlexibleConfigurationSourceProvider;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.zookeeper.discovery.CuratorBundle;
import org.apache.curator.test.InstanceSpec;
import java.io.Closeable;
import java.util.List;
import java.util.Random;

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
        bootstrap.setConfigurationSourceProvider(new FlexibleConfigurationSourceProvider());
        bootstrap.addBundle(new CuratorBundle<>());
        bootstrap.addBundle(new SqlBundle<>());
        bootstrap.addBundle(new SoaBundle<>());
        bootstrap.addBundle(new SoaClientBundle<>());
    }

    @Override
    public void run(String... arguments) throws Exception
    {
        if ( arguments.length == 0 )
        {
            super.run(setSystemAndAdjustArgs(configFqpn));
        }
        else
        {
            super.run(arguments);
        }
    }

    public static String[] setSystemAndAdjustArgs(String configFqpn)
    {
        System.setProperty("dw.curator.connectionString", "localhost:2181");
        System.setProperty("dw.soa.instanceName", "instance-" + new Random().nextInt(10000));
        System.setProperty("dw.soa.discovery.type", "zookeeper");
        System.setProperty("dw.soa.discovery.bindAddress", "localhost");
        System.setProperty("dw.sql.mybatisConfigUrl", "example-mybatis.xml");
        System.setProperty("dw.soa.attributes.type", "sql");
        System.setProperty("dw.server.applicationConnectors[0].port", "" + InstanceSpec.getRandomPort());
        System.setProperty("dw.server.adminConnectors[0].port", "" + InstanceSpec.getRandomPort());
        return new String[]
        {
            "server",
            "|" + configFqpn
        };
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception
    {
        environment.lifecycle().manage(this);

        internalRun(configuration, environment);

        SoaInfo info = SoaBundle.getFeatures(environment).getSoaInfo();
        System.err.println("Main port: " + info.getMainPort());
        System.err.println("Admin port: " + info.getAdminPort());
    }

    protected abstract void internalRun(Configuration configuration, Environment environment);

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
