package io.soabase.core;

import io.dropwizard.Configuration;

public interface ConfigurationAccessor<C extends Configuration, T>
{
    public T accessConfiguration(C configuration);
}
