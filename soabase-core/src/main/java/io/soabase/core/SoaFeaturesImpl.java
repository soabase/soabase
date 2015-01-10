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

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.soabase.core.features.ExecutorBuilder;
import io.soabase.core.features.attributes.DynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

class SoaFeaturesImpl implements SoaFeatures
{
    private final ConcurrentMap<Class<?>, ConcurrentMap<String, Object>> named = Maps.newConcurrentMap();
    private final SoaDiscovery discovery;
    private final DynamicAttributes dynamicAttributes;
    private final SoaInfo info;
    private final ExecutorBuilder executorBuilder;

    public SoaFeaturesImpl()
    {
        this(null, null, null, null);
    }

    SoaFeaturesImpl(SoaDiscovery discovery, DynamicAttributes dynamicAttributes, SoaInfo info, ExecutorBuilder executorBuilder)
    {
        this.discovery = discovery;
        this.dynamicAttributes = dynamicAttributes;
        this.info = info;
        this.executorBuilder = executorBuilder;
    }

    @Override
    public <T> Collection<String> getNames(Class<T> clazz)
    {
        ConcurrentMap<String, Object> map = named.get(clazz);
        return (map != null) ? map.keySet() : ImmutableSet.<String>of();
    }

    void setNamed(SoaFeaturesImpl from)
    {
        this.named.clear();
        this.named.putAll(from.named);
    }

    @Override
    public ExecutorBuilder getExecutorBuilder()
    {
        return executorBuilder;
    }

    @Override
    public SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    @Override
    public DynamicAttributes getAttributes()
    {
        return dynamicAttributes;
    }

    @Override
    public SoaInfo getSoaInfo()
    {
        return info;
    }

    @Override
    public <T> T getNamedRequired(Class<T> clazz, String name)
    {
        return Preconditions.checkNotNull(getNamed(clazz, name), String.format("No object found for \"%s\" of type \"%s\"", name, clazz.getName()));
    }

    @Override
    public <T> T getNamed(Class<T> clazz, String name)
    {
        name = Preconditions.checkNotNull(name, "name cannot be null");
        clazz = Preconditions.checkNotNull(clazz, "clazz cannot be null");

        Map<String, Object> map = named.get(clazz);
        Object o = (map != null) ? map.get(name) : null;
        return (o != null) ? clazz.cast(o) : null;
    }

    @Override
    public <T> void putNamed(T o, Class<T> clazz, String name)
    {
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
}
