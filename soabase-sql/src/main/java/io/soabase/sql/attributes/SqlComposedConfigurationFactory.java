package io.soabase.sql.attributes;

import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.ComposedConfigurationFactory;

public class SqlComposedConfigurationFactory implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder builder)
    {
        builder.add("sql", SqlConfiguration.class);
    }
}
