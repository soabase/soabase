package io.soabase.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import javax.validation.Validator;
import java.util.ServiceLoader;

public class ComposedConfigurationFactoryFactory implements ConfigurationFactoryFactory<ComposedConfiguration>
{
    private final ComposedConfigurationBuilder builder;

    public static ComposedConfigurationBuilder builderFromServices()
    {
        ComposedConfigurationBuilder<ComposedConfiguration> builder = new ComposedConfigurationBuilder<>(ComposedConfiguration.class);
        ServiceLoader<ComposedConfigurationFactory> serviceLoader = ServiceLoader.load(ComposedConfigurationFactory.class);
        for ( ComposedConfigurationFactory factory : Lists.newArrayList(serviceLoader.iterator()) )
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

    public static ComposedConfigurationFactoryFactory fromServices()
    {
        return new ComposedConfigurationFactoryFactory(builderFromServices());
    }

    public ComposedConfigurationFactoryFactory(ComposedConfigurationBuilder builder)
    {
        this.builder = builder;
    }

    @Override
    public ConfigurationFactory<ComposedConfiguration> create(Class<ComposedConfiguration> klass, Validator validator, ObjectMapper objectMapper, String propertyPrefix)
    {
        return new ConfigurationFactory<>(builder.build(), validator, objectMapper, propertyPrefix);
    }
}
