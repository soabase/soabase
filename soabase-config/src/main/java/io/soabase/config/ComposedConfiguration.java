package io.soabase.config;

import io.dropwizard.Configuration;
import java.lang.reflect.Method;

public class ComposedConfiguration extends Configuration
{
    public <T> T as(Class<T> clazz)
    {
        try
        {
            Method method = getClass().getMethod(ComposedConfigurationBuilder.getterName(clazz));
            Object value = method.invoke(this);
            return clazz.cast(value);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }
}
