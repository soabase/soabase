package io.soabase.example;

import com.google.common.collect.Lists;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.client.SoaClientBundle;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaCli;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaFeatures;
import io.soabase.example.mocks.MockDatabase;
import io.soabase.example.mocks.MockZooKeeper;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorBundle;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import org.apache.ibatis.session.SqlSession;
import java.io.Closeable;
import java.util.List;

public class ExampleApp extends Application<ExampleConfiguration> implements Managed
{
    private final List<Closeable> closeables = Lists.newArrayList();

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
        ConfigurationAccessor<ExampleConfiguration, SqlConfiguration> sqlAccessor = new ConfigurationAccessor<ExampleConfiguration, SqlConfiguration>()
        {
            @Override
            public SqlConfiguration accessConfiguration(ExampleConfiguration configuration)
            {
                return configuration.getSqlConfiguration();
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
        ConfigurationAccessor<ExampleConfiguration, HttpClientConfiguration> clientAccessor = new ConfigurationAccessor<ExampleConfiguration, HttpClientConfiguration>()
        {
            @Override
            public HttpClientConfiguration accessConfiguration(ExampleConfiguration configuration)
            {
                return configuration.getClientConfiguration();
            }
        };
        bootstrap.addBundle(new CuratorBundle<>(soaAccessor, curatorAccessor));
        bootstrap.addBundle(new SqlBundle<>(soaAccessor, sqlAccessor));
        bootstrap.addBundle(new SoaBundle<>(soaAccessor));
        bootstrap.addBundle(new SoaClientBundle<>(soaAccessor, clientAccessor, "example"));
    }

    @Override
    public void run(String... arguments) throws Exception
    {
        MockZooKeeper mockZooKeeper = new MockZooKeeper();
        closeables.add(mockZooKeeper);

        if ( arguments.length == 0 )
        {
            arguments = new String[]
            {
                "-o",
                "curator.connectionString=" + mockZooKeeper.getConnectionString(),
                "-o",
                "discovery.type=zookeeper",
                "-o",
                "attributes.type=sql",
                "-o",
                "sql.mybatisConfigUrl=test-mybatis.xml"
            };
        }

        super.run(SoaCli.parseArgs(arguments));
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception
    {
        closeables.add(new MockDatabase(configuration.getSoaConfiguration().getNamed(SqlSession.class, SoaFeatures.DEFAULT_NAME)));
        environment.lifecycle().manage(this);
    }

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
