package io.soabase.zookeeper.discovery;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.CheckedConfigurationAccessor;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;

public class CuratorBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    private final ConfigurationAccessor<T, CuratorConfiguration> curatorAccessor;
    private final ConfigurationAccessor<T, SoaConfiguration> soaAccessor;

    public CuratorBundle(ConfigurationAccessor<T, SoaConfiguration> soaAccessor, ConfigurationAccessor<T, CuratorConfiguration> curatorAccessor)
    {
        this.soaAccessor = new CheckedConfigurationAccessor<>(soaAccessor);
        this.curatorAccessor = new CheckedConfigurationAccessor<>(curatorAccessor);
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        CuratorConfiguration curatorConfiguration = curatorAccessor.accessConfiguration(configuration);
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

        SoaConfiguration soaConfiguration = soaAccessor.accessConfiguration(configuration);
        soaConfiguration.putNamed(curator, CuratorFramework.class, curatorConfiguration.getCuratorName());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }
}
