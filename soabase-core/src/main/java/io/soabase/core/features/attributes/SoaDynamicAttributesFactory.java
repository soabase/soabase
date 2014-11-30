package io.soabase.core.features.attributes;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import io.dropwizard.setup.Environment;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = NullDynamicAttributesFactory.class)
public interface SoaDynamicAttributesFactory extends Discoverable
{
    public SoaDynamicAttributes build(Environment environment, String groupName, String instanceName);
}
