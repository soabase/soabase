package io.soabase.core.features.discovery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultDiscoveryHealth.class)
public interface SoaDiscoveryHealthFactory
{
    public SoaDiscoveryHealth build(SoaConfiguration configuration, Environment environment);
}
