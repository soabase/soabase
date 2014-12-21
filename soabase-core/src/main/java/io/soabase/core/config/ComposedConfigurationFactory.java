package io.soabase.core.config;

import io.dropwizard.jackson.Discoverable;

public interface ComposedConfigurationFactory extends Discoverable
{
    public void addToBuilder(ComposedConfigurationBuilder builder);
}
