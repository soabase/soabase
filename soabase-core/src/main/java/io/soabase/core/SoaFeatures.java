package io.soabase.core;

import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;

public interface SoaFeatures
{
    public static final String DEFAULT_NAME = "";

    public <T> T getNamed(Class<T> clazz, String name);

    public SoaDiscovery getDiscovery();

    public SoaDynamicAttributes getAttributes();
}
