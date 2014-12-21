package io.soabase.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.dropwizard.Configuration;
import java.lang.reflect.Field;

public class ComposedConfiguration extends Configuration
{
    private final CacheLoader<Class<?>, Field> loader = new CacheLoader<Class<?>, Field>()
    {
        @Override
        public Field load(Class<?> key) throws Exception
        {
            for ( Field field : ComposedConfiguration.this.getClass().getDeclaredFields() )
            {
                if ( field.getType().equals(key) )
                {
                    return field;
                }
            }
            throw new Exception("No field found for: " + key);
        }
    };
    private final LoadingCache<Class<?>, Field> fieldCache = CacheBuilder.newBuilder().build(loader);

    public <T> T as(Class<T> clazz)
    {
        try
        {
            Field field = fieldCache.get(clazz);
            Object value = field.get(this);
            return clazz.cast(value);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }
}
