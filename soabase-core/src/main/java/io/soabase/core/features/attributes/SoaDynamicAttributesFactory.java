package io.soabase.core.features.attributes;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = NullDynamicAttributesFactory.class)
public interface SoaDynamicAttributesFactory extends Discoverable
{
    public SoaDynamicAttributes build(SoaConfiguration configuration, Environment environment, List<String> scopes);
}
