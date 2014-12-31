/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.core.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.lang.reflect.Field;

/**
 * Simple way of allowing shared bundles, etc. to access their custom configuration
 * objects.
 */
public class ComposedConfigurationAccessor
{
    private final LoadingCache<Class<?>, Field> fieldCache;
    private final Object configuration;

    /**
     * @param configuration The application configuration object
     */
    public ComposedConfigurationAccessor(final Object configuration)
    {
        this.configuration = configuration;
        CacheLoader<? super Class<?>, Field> loader = new CacheLoader<Class<?>, Field>()
        {
            @Override
            public Field load(Class<?> clazz) throws Exception
            {
                Class<?> configurationClass = configuration.getClass();
                while ( configurationClass != null )
                {
                    for ( Field field : configurationClass.getDeclaredFields() )
                    {
                        if ( field.getType().equals(clazz) )
                        {
                            field.setAccessible(true);
                            return field;
                        }
                    }
                    configurationClass = configurationClass.getSuperclass();
                }
                // TODO logging
                throw new Exception("Could not find a field of the type: " + clazz);
            }
        };
        fieldCache = CacheBuilder.newBuilder().build(loader);
    }

    /**
     * Access a custom configuration of the given type. The fields of the configuration object
     * are searched for one that is of the given type.
     *
     * @param clazz type to look for
     * @return the configuration
     */
    public <T> T access(Class<T> clazz)
    {
        try
        {
            return clazz.cast(fieldCache.get(clazz).get(configuration));
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    /**
     * Return true if there is a custom configuration of the given type.
     *
     * @param clazz type to loof for
     * @return true/false
     */
    public <T> boolean has(Class<T> clazz)
    {
        return (fieldCache.getIfPresent(clazz) != null);
    }
}
