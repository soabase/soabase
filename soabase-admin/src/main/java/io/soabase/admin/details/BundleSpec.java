package io.soabase.admin.details;

import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;

public class BundleSpec<T extends Configuration>
{
    private final Bundle bundle;
    private final Phase phase;
    private final ConfiguredBundle<T> configuredBundle;

    public enum Phase
    {
        PRE_SOA,
        POST_SOA
    }

    public BundleSpec(Bundle bundle, Phase phase)
    {
        this.bundle = bundle;
        this.configuredBundle = null;
        this.phase = phase;
    }

    public BundleSpec(ConfiguredBundle<T> configuredBundle, Phase phase)
    {
        this.bundle = null;
        this.configuredBundle = configuredBundle;
        this.phase = phase;
    }

    public Bundle getBundle()
    {
        return bundle;
    }

    public Phase getPhase()
    {
        return phase;
    }

    public ConfiguredBundle<T> getConfiguredBundle()
    {
        return configuredBundle;
    }
}
