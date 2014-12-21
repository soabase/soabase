package io.soabase.zookeeper.discovery;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.service.ComposedConfigurationServiceFactory;

public class CuratorComposedConfigurationFactory implements ComposedConfigurationServiceFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder<?> builder)
    {
        builder.add("curator", CuratorConfiguration.class);
    }
}
