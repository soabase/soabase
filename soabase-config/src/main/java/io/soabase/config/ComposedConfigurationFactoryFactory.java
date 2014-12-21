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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import javax.validation.Validator;

/**
 * Dropwizard {@link ConfigurationFactoryFactory} that uses a composed configuration builder
 * to generate the configuration instance
 */
public class ComposedConfigurationFactoryFactory<T extends ComposedConfiguration> implements ConfigurationFactoryFactory<T>
{
    private final ComposedConfigurationBuilder<T> builder;

    /**
     * @param builder builder to use
     */
    public ComposedConfigurationFactoryFactory(ComposedConfigurationBuilder<T> builder)
    {
        this.builder = builder;
    }

    @Override
    public ConfigurationFactory<T> create(Class<T> klass, Validator validator, ObjectMapper objectMapper, String propertyPrefix)
    {
        return new ConfigurationFactory<>(builder.build(), validator, objectMapper, propertyPrefix);
    }
}
