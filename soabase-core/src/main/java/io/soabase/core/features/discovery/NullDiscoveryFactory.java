package io.soabase.core.features.discovery;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;

@JsonTypeName("default")
public class NullDiscoveryFactory implements SoaDiscoveryFactory
{
    @Override
    public SoaDiscovery build(SoaConfiguration configuration, Environment environment)
    {
        return new SoaDiscovery()
        {
            @Override
            public SoaDiscoveryInstance getInstance(String serviceName)
            {
                return null;
            }

            @Override
            public void noteError(String serviceName, SoaDiscoveryInstance errorInstance)
            {
                // NOP
            }
        };
    }
}
