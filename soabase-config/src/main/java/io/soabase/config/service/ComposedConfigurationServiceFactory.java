package io.soabase.config.service;

import io.soabase.config.ComposedConfigurationBuilder;

/**
 * Service/Bundles can provide an instance of this factory and add a service
 * provider-configuration file. {@link FromServices} can then be used
 * to generate a new {@link io.soabase.config.ComposedConfiguration} instance.
 */
public interface ComposedConfigurationServiceFactory
{
    /**
     * Add configuration field types to the builder
     *
     * @param builder the composed configuration builder
     */
    public void addToBuilder(ComposedConfigurationBuilder<?> builder);
}
