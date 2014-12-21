package io.soabase.sql.attributes;

import io.soabase.core.config.ComposedConfigurationBuilder;
import io.soabase.core.config.ComposedConfigurationFactory;

public class SqlComposedConfigurationFactory implements ComposedConfigurationFactory
{
    @Override
    public void addToBuilder(ComposedConfigurationBuilder builder)
    {
        builder.add(SqlBundle.CONFIGURATION_NAME, new SqlConfiguration());
    }
}
