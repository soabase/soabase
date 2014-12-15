/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.dropwizard.Configuration;
import io.soabase.core.features.attributes.NullDynamicAttributesFactory;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import io.soabase.core.features.discovery.DefaultDiscoveryHealthFactory;
import io.soabase.core.features.discovery.NullDiscoveryFactory;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import io.soabase.core.features.discovery.SoaDiscoveryHealthFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoaConfiguration extends Configuration implements SoaFeatures
{
    @Valid
    @NotNull
    private SoaDiscoveryFactory discoveryFactory = new NullDiscoveryFactory();

    @Valid
    @NotNull
    private SoaDiscoveryHealthFactory discoveryHealthFactory = new DefaultDiscoveryHealthFactory();

    @Valid
    @NotNull
    private SoaDynamicAttributesFactory attributesFactory = new NullDynamicAttributesFactory();

    @Valid
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Service Names can only contain letters and numbers")
    private String thisServiceName;

    @Valid
    private int shutdownWaitMaxMs = (int)TimeUnit.SECONDS.toMillis(5);

    @Valid
    private String instanceName;

    @Valid
    private List<String> scopes = ImmutableList.of();

    @Valid
    private boolean addCorsFilter = false;

    @Valid
    private int discoveryHealthCheckPeriodMs = (int)TimeUnit.SECONDS.toMillis(10);

    @Valid
    private String adminJerseyPath = "/api";

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final long startTimeMs = System.currentTimeMillis();

    private volatile SoaDiscovery discovery;
    private volatile SoaDynamicAttributes attributes;
    private final ConcurrentMap<Class<?>, ConcurrentMap<String, Object>> named = Maps.newConcurrentMap();

    @JsonProperty("checkPeriodMs")
    public int getDiscoveryHealthCheckPeriodMs()
    {
        return discoveryHealthCheckPeriodMs;
    }

    @JsonProperty("checkPeriodMs")
    public void setDiscoveryHealthCheckPeriodMs(int discoveryHealthCheckPeriodMs)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.discoveryHealthCheckPeriodMs = discoveryHealthCheckPeriodMs;
    }

    @JsonProperty("discovery")
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

    @JsonProperty("discoveryHealth")
    public SoaDiscoveryHealthFactory getDiscoveryHealthFactory()
    {
        return discoveryHealthFactory;
    }

    @JsonProperty("discoveryHealth")
    public void setDiscoveryHealthFactory(SoaDiscoveryHealthFactory discoveryHealthFactory)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.discoveryHealthFactory = discoveryHealthFactory;
    }

    @JsonProperty("adminJerseyPath")
    public String getAdminJerseyPath()
    {
        return adminJerseyPath;
    }

    @JsonProperty("adminJerseyPath")
    public void setAdminJerseyPath(String adminJerseyPath)
    {
        this.adminJerseyPath = adminJerseyPath;
    }

    @JsonProperty("thisServiceName")
    public String getThisServiceName()
    {
        return thisServiceName;
    }

    @JsonProperty("thisServiceName")
    public void setThisServiceName(String thisServiceName)
    {
        this.thisServiceName = thisServiceName;
    }

    public void lock()
    {
        locked.set(true);
    }

    public <T> T getNamedRequired(Class<T> clazz, String name)
    {
        return Preconditions.checkNotNull(getNamed(clazz, name), String.format("No object found for \"%s\" of type \"%s\"", name, clazz.getName()));
    }

    public <T> T getNamed(Class<T> clazz, String name)
    {
        name = Preconditions.checkNotNull(name, "name cannot be null");
        clazz = Preconditions.checkNotNull(clazz, "clazz cannot be null");

        Map<String, Object> map = named.get(clazz);
        Object o = (map != null) ? map.get(name) : null;
        return (o != null) ? clazz.cast(o) : null;
    }

    public <T> void putNamed(T o, Class<T> clazz, String name)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");

        clazz = Preconditions.checkNotNull(clazz, "clazz cannot be null");
        name = Preconditions.checkNotNull(name, "name cannot be null");
        o = Preconditions.checkNotNull(o, "object cannot be null");
        Preconditions.checkArgument(name.length() > 0, "name cannot be the empty string");

        ConcurrentMap<String, Object> newMap = Maps.newConcurrentMap();
        ConcurrentMap<String, Object> oldMap = named.putIfAbsent(clazz, newMap);
        ConcurrentMap<String, Object> useMap = (oldMap != null) ? oldMap : newMap;

        Object old = useMap.putIfAbsent(name, o);
        Preconditions.checkArgument(old == null, "Named value already set for: " + name + " and " + clazz.getName());
    }

    @Override
    public SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    public void setDiscovery(SoaDiscovery discovery)
    {
        Preconditions.checkState(!locked.get(), "Configuration has been locked and cannot be modified");
        this.discovery = discovery;
    }

    @Override
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
