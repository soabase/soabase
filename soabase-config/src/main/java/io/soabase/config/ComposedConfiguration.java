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
