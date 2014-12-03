package io.soabase.core.features;

import io.dropwizard.Bundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AdminJerseyBundle implements Bundle
{
    private final JerseyEnvironment jerseyEnvironment;

    public AdminJerseyBundle(JerseyEnvironment jerseyEnvironment)
    {
        this.jerseyEnvironment = jerseyEnvironment;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    @Override
    public void run(Environment environment)
    {

    }
}
