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
package io.soabase.guice;

import com.google.common.base.Preconditions;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import java.util.List;

/**
 * Version of {@link StandardInjectorProvider} that binds the configuration object
 */
public class ConfigurationInjectorProvider<T extends Configuration> extends StandardInjectorProvider<T>
{
    private final TypeLiteral<T> configurationType;

    /**
     * This version binds based on {@link Object#getClass()}
     *
     * @param modules additional modules
     */
    public ConfigurationInjectorProvider(Module... modules)
    {
        super(modules);
        configurationType = null;
    }

    /**
     * @param configurationType type to bind to
     * @param modules additional modules
     */
    public ConfigurationInjectorProvider(Class<T> configurationType, Module... modules)
    {
        super(modules);
        this.configurationType = TypeLiteral.get(configurationType);
    }

    /**
     * @param configurationType type to bind to
     * @param modules additional modules
     */
    public ConfigurationInjectorProvider(TypeLiteral<T> configurationType, Module... modules)
    {
        super(modules);
        this.configurationType = Preconditions.checkNotNull(configurationType, "configurationType cannot be null");
    }

    @Override
    protected void internalAddModules(List<Module> localModules, T configuration, Environment environment)
    {
        Module configurationModule = binder -> {
            if ( configurationType != null )
            {
                binder.bind(configurationType).toInstance(configuration);
            }
            else
            {
                @SuppressWarnings("unchecked") Class<T> clazz = (Class<T>)configuration.getClass();
                binder.bind(clazz).toInstance(configuration);
            }
        };
        localModules.add(configurationModule);
    }
}
