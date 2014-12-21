package io.soabase.client;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.ComposedConfigurationFactory;

public class ClientComposedConfigurationFactory implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder builder)
    {
        builder.add("client", SoaClientConfiguration.class);
    }
}
