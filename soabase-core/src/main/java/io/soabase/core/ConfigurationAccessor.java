package io.soabase.core;

import io.dropwizard.Configuration;

public interface ConfigurationAccessor<C extends Configuration>
{
    public <T> T accessConfiguration(C configuration, Class<T> clazz);
}
