package io.soabase.core.features.discovery;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;

@JsonTypeName("default")
public class NullDiscoveryFactory implements SoaDiscoveryFactory
{
    @Override
    public SoaDiscovery build(Environment environment)
    {
        return new SoaDiscovery()
        {
            @Override
            public SoaDiscoveryInstance getInstance(String serviceName)
            {
                return null;
            }

            @Override
            public void noteError(SoaDiscoveryInstance service)
            {
                // NOP
            }
        };
    }
}
