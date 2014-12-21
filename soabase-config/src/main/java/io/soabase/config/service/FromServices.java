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
package io.soabase.config.service;

import com.google.common.collect.Lists;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.soabase.config.ComposedConfiguration;
import io.soabase.config.ComposedConfigurationBuilder;
import io.soabase.config.ComposedConfigurationFactoryFactory;
import java.util.ServiceLoader;

/**
 * <p>
 *     Utility to generate {@link ComposedConfiguration} instance, {@link ComposedConfigurationBuilder}
 *     or a Dropwizard {@link ConfigurationFactoryFactory} from {@link ServiceLoader} based
 *     {@link ComposedConfigurationServiceFactory} instances
 * </p>
 *
 * <p>
 *     To add your own ComposedConfigurationServiceFactory instances:
 *     <li>Create one or more classes that implement ComposedConfigurationServiceFactory</li>
 *     <li>Add a file named "io.soabase.config.service.ComposedConfigurationServiceFactory" in a directory named "META-INF/services" to your project</li>
 *     <li>This file should contain the FQCN for each of your classes, one per line</li>
 * </p>
 */
public class FromServices<T extends ComposedConfiguration>
{
    private String fqClassName = ComposedConfigurationBuilder.DEFAULT_COMPOSED_FQ_CLASS_NAME;
    private Class<T> baseClass;
    private ClassLoader classLoader = null;

    /**
     * Return a Dropwizard {@link ConfigurationFactoryFactory} that creates a standard
     * {@link ComposedConfiguration} using {@link ComposedConfigurationServiceFactory} instances
     * from the default class loader.
     *
     * @return standard factory
     */
    public static ConfigurationFactoryFactory<ComposedConfiguration> standardFactory()
    {
        return FromServices.create().withBaseClass(ComposedConfiguration.class).factory();
    }

    /**
     * Create a new configuration builder that extends {@link ComposedConfiguration} using {@link ComposedConfigurationServiceFactory} instances
     * from the default class loader.
     *
     * @return configuration builder
     */
    public static ComposedConfigurationBuilder<ComposedConfiguration> standardBuilder()
    {
        return FromServices.create().withBaseClass(ComposedConfiguration.class).builder();
    }

    /**
     * Start a new configuration services loader

     * @return loader
     */
    public static <T extends ComposedConfiguration> FromServices<T> create()
    {
        return new FromServices<>();
    }

    /**
     * Required - the base class for the configuration class
     *
     * @param baseClass bass class
     * @return this
     */
    public FromServices<T> withBaseClass(Class<T> baseClass)
    {
        this.baseClass = baseClass;
        return this;
    }

    /**
     * Optional - change the FQCN of the generated configuration class from the default.
     *
     * @param fqClassName new FQCN
     * @return this
     */
    public FromServices<T> withFqClassName(String fqClassName)
    {
        this.fqClassName = fqClassName;
        return this;
    }

    /**
     * Optional - change the class loader used to load services from the default
     *
     * @param classLoader class loader
     * @return this
     */
    public FromServices<T> withClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
        return this;
    }

    /**
     * Generate a new configuration class and a new configuration instance based on the current values
     *
     * @return new c
     */
    public T instance()
    {
        ComposedConfigurationBuilder<T> builder = new ComposedConfigurationBuilder<>(fqClassName, baseClass);
        try
        {
            return builder.build().newInstance();
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a new configuration class builder based on the current values
     *
     * @return configuration class builder
     */
    public ComposedConfigurationBuilder<T> builder()
    {
        ComposedConfigurationBuilder<T> builder = new ComposedConfigurationBuilder<>(fqClassName, baseClass);
        ServiceLoader<ComposedConfigurationServiceFactory> serviceLoader = (classLoader != null) ? ServiceLoader.load(ComposedConfigurationServiceFactory.class, classLoader) : ServiceLoader.load(ComposedConfigurationServiceFactory.class);
        for ( ComposedConfigurationServiceFactory factory : Lists.newArrayList(serviceLoader.iterator()) )
        {
            try
            {
                factory.addToBuilder(builder);
            }
            catch ( Exception e )
            {
                // TODO logging
                throw new RuntimeException(e);
            }
        }
        return builder;
    }

    /**
     * Generate a new Dropwizard {@link ConfigurationFactoryFactory} based on the current values
     *
     * @return Dropwizard {@link ConfigurationFactoryFactory}
     */
    public ConfigurationFactoryFactory<T> factory()
    {
        return new ComposedConfigurationFactoryFactory<>(builder());
    }

    private FromServices()
    {
    }
}
