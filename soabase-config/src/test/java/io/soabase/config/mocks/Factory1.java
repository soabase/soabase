package io.soabase.config.mocks;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.ComposedConfigurationFactory;

public class Factory1 implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder<?> builder)
    {
        builder.add("t1", TestConfiguration1.class);
    }
}
