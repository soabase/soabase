package io.soabase.zookeeper.discovery;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;

public class CuratorBundle<T extends SoaConfiguration & CuratorConfigurationAccessor> implements ConfiguredBundle<T>
{
    public static CuratorFramework getCuratorFramework(SoaConfiguration configuration)
    {
        return configuration.getNamed(CuratorFramework.class, CuratorBundle.class.getName());
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        CuratorConfiguration curatorConfiguration = configuration.getCuratorConfiguration();
        // TODO more config
        final CuratorFramework curator = CuratorFrameworkFactory.newClient(curatorConfiguration.getConnectionString(), new RetryOneTime(1));

        Managed managed = new Managed()
        {
            @Override
            public void start() throws Exception
            {
                curator.start();
            }

            @Override
            public void stop() throws Exception
            {
                CloseableUtils.closeQuietly(curator);
            }
        };
        environment.lifecycle().manage(managed);

        configuration.putNamed(curator, CuratorBundle.class.getName());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }
}
