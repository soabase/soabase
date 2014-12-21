package io.soabase.config.mocks;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.ComposedConfigurationFactory;

public class Factory2 implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder<?> builder)
    {
        builder.add("t2", TestConfiguration2.class);
    }
}
