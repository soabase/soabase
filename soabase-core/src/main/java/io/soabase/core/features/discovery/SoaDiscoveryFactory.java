package io.soabase.core.features.discovery;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.dropwizard.setup.Environment;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = NullDiscoveryFactory.class)
public interface SoaDiscoveryFactory extends Discoverable
{
    public SoaDiscovery build(Environment environment);
}
