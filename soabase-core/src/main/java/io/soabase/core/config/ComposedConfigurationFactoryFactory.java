package io.soabase.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import javax.validation.Validator;

public class ComposedConfigurationFactoryFactory implements ConfigurationFactoryFactory<ComposedConfiguration>
{
    @Override
    public ConfigurationFactory<ComposedConfiguration> create(Class<ComposedConfiguration> klass, Validator validator, ObjectMapper objectMapper, String propertyPrefix)
    {
        return new ConfigurationFactory<ComposedConfiguration>(klass, validator, objectMapper, propertyPrefix);
    }
}
