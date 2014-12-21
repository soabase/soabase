package io.soabase.core.config;

import io.soabase.core.SoaBundle;
import io.soabase.core.SoaConfiguration;

public class SoaComposedConfigurationFactory implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder builder)
    {
        builder.add(SoaBundle.CONFIGURATION_NAME, new SoaConfiguration());
    }
}
