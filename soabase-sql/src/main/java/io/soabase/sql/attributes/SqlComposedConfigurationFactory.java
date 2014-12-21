package io.soabase.sql.attributes;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.service.ComposedConfigurationServiceFactory;

public class SqlComposedConfigurationFactory implements ComposedConfigurationServiceFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder<?> builder)
    {
        builder.add("sql", SqlConfiguration.class);
    }
}
