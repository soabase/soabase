package io.soabase.core.features.discovery;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;

@JsonTypeName("default")
public class DefaultDiscoveryHealthFactory implements SoaDiscoveryHealthFactory
{
    public SoaDiscoveryHealth build(SoaConfiguration configuration, Environment environment)
    {
        return new DefaultDiscoveryHealth();
    }
}
