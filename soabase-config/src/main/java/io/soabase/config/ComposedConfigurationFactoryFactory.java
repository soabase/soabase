package io.soabase.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import javax.validation.Validator;

public class ComposedConfigurationFactoryFactory<T extends ComposedConfiguration> implements ConfigurationFactoryFactory<T>
{
    private final ComposedConfigurationBuilder<T> builder;

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
