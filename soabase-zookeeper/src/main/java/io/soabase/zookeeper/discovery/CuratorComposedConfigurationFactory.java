package io.soabase.zookeeper.discovery;

import io.soabase.core.config.ComposedConfigurationBuilder;
import io.soabase.core.config.ComposedConfigurationFactory;

public class CuratorComposedConfigurationFactory implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder builder)
    {
        builder.add(CuratorBundle.CONFIGURATION_NAME, new CuratorConfiguration());
    }
}
