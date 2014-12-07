package io.soabase.core;

import com.google.common.base.Preconditions;
import io.dropwizard.Configuration;

public class CheckedConfigurationAccessor<C extends Configuration> implements ConfigurationAccessor<C>
{
    private final ConfigurationAccessor<C> accessor;

    public CheckedConfigurationAccessor(ConfigurationAccessor<C> accessor)
    {
        this.accessor = Preconditions.checkNotNull(accessor, "accessor cannot be null");
    }

    @Override
    public <T> T accessConfiguration(C configuration, Class<T> clazz)
    {
        T reference = accessor.accessConfiguration(configuration, clazz);
        return Preconditions.checkNotNull(reference, "Could not access configuration: " + clazz.getName());
    }
}
