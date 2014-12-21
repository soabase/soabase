package io.soabase.core.config;

import io.dropwizard.Configuration;

public class ComposedConfiguration extends Configuration
{
    public <T> T access(String name, Class<T> clazz)
    {
        try
        {
            Object value = getClass().getMethod(ComposedConfigurationBuilder.getterName(name)).invoke(this);
            return clazz.cast(value);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }
}
