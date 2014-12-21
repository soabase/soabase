package io.soabase.core.features;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.ComposedConfigurationFactory;
import io.soabase.core.SoaConfiguration;

public class SoaComposedConfigurationFactory implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder<?> builder)
    {
        builder.add("soa", SoaConfiguration.class);
    }
}
