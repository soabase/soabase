package io.soabase.config.mocks;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.service.ComposedConfigurationServiceFactory;

public class Factory2 implements ComposedConfigurationServiceFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder<?> builder)
    {
        builder.add("t2", TestConfiguration2.class);
    }
}
