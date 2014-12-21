package io.soabase.config;

import io.dropwizard.Configuration;
import java.lang.reflect.Method;

/**
 * Required base class for Composed Configurations. The only
 * addition to {@link Configuration} is a method to get the
 * contained/generated configuration instances.
 */
public abstract class ComposedConfiguration extends Configuration
{
    /**
     * Return the generated/contained configuration of the given type
     *
     * @param clazz configuration type
     * @return the configuration instance
     */
    public <T> T as(Class<T> clazz)
    {
        Method method;
        try
        {
            method = getClass().getMethod(ComposedConfigurationBuilder.getterName(clazz));
        }
        catch ( NoSuchMethodException e )
        {
            // TODO logging
            String message = "There is no contained configuration of type " + clazz.getSimpleName();
            throw new RuntimeException(message, e);
        }

        try
        {
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
