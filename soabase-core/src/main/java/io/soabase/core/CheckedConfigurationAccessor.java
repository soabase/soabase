package io.soabase.core;

import com.google.common.base.Preconditions;
import io.dropwizard.Configuration;

public class CheckedConfigurationAccessor<C extends Configuration, T> implements ConfigurationAccessor<C, T>
{
    private final ConfigurationAccessor<C, T> accessor;

    public CheckedConfigurationAccessor(ConfigurationAccessor<C, T> accessor)
    {
        this.accessor = Preconditions.checkNotNull(accessor, "accessor cannot be null");
    }

    @Override
    public T accessConfiguration(C configuration)
    {
        T reference = accessor.accessConfiguration(configuration);
        return Preconditions.checkNotNull(reference, "Could not access configuration via accessor: " + accessor.getClass().getName());
    }
}
