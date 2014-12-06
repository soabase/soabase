package io.soabase.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.dropwizard.Configuration;
import io.soabase.core.features.attributes.NullDynamicAttributesFactory;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import io.soabase.core.features.discovery.NullDiscoveryFactory;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoaConfiguration extends Configuration
{
    @Valid
    @NotNull
    private volatile SoaDiscoveryFactory discoveryFactory = new NullDiscoveryFactory();

    @Valid
    @NotNull
    private volatile SoaDynamicAttributesFactory attributesFactory = new NullDynamicAttributesFactory();

    @Valid
    private volatile int shutdownWaitMaxMs = (int)TimeUnit.SECONDS.toMillis(5);

    @Valid
    private volatile String instanceName;

    @Valid
    private volatile List<String> scopes = ImmutableList.of();

    @Valid
    private volatile boolean addCorsFilter = false;

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private volatile SoaDiscovery discovery;
    private volatile SoaDynamicAttributes attributes;
    private final ConcurrentMap<String, Object> named = Maps.newConcurrentMap();

    public SoaDiscoveryFactory getDiscoveryFactory()
    {
        return discoveryFactory;
    }

    @JsonProperty("discovery")
    public void setDiscoveryFactory(SoaDiscoveryFactory discoveryFactory)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.discoveryFactory = discoveryFactory;
    }

    @JsonProperty("attributes")
    public SoaDynamicAttributesFactory getAttributesFactory()
    {
        return attributesFactory;
    }

    @JsonProperty("attributes")
    public void setAttributesFactory(SoaDynamicAttributesFactory attributesFactory)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.attributesFactory = attributesFactory;
    }

    @JsonProperty("shutdownWaitMaxMs")
    public int getShutdownWaitMaxMs()
    {
        return shutdownWaitMaxMs;
    }

    @JsonProperty("shutdownWaitMaxMs")
    public void setShutdownWaitMaxMs(int shutdownWaitMaxMs)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.shutdownWaitMaxMs = shutdownWaitMaxMs;
    }

    @JsonProperty("instanceName")
    public String getInstanceName()
    {
        return instanceName;
    }

    @JsonProperty("instanceName")
    public void setInstanceName(String instanceName)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.instanceName = instanceName;
    }

    @JsonProperty("additionalScopes")
    public List<String> getScopes()
    {
        return scopes;
    }

    @JsonProperty("additionalScopes")
    public void setScopes(List<String> scopes)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.scopes = ImmutableList.copyOf(scopes);
    }

    @JsonProperty("addCorsFilter")
    public boolean isAddCorsFilter()
    {
        return addCorsFilter;
    }

    @JsonProperty("addCorsFilter")
    public void setAddCorsFilter(boolean addCorsFilter)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.addCorsFilter = addCorsFilter;
    }

    public void lock()
    {
        locked.set(true);
    }

    public <T> T getNamed(Class<T> clazz, String name)
    {
        Object o = named.get(name);
        return (o != null) ? clazz.cast(o) : null;
    }

    public <T> void putNamed(T o, String name)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        o = Preconditions.checkNotNull(o, "o cannot be null");
        Object old = named.putIfAbsent(name, o);
        Preconditions.checkArgument(old == null, "Named value already set for: " + name);
    }

    public SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    public void setDiscovery(SoaDiscovery discovery)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.discovery = discovery;
    }

    public SoaDynamicAttributes getAttributes()
    {
        return attributes;
    }

    public void setAttributes(SoaDynamicAttributes attributes)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.attributes = attributes;
    }
}
