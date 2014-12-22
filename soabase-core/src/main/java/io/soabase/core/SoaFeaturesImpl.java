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
import com.google.common.collect.Maps;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

class SoaFeaturesImpl implements SoaFeatures
{
    private final ConcurrentMap<Class<?>, ConcurrentMap<String, Object>> named = Maps.newConcurrentMap();
    private final SoaDiscovery discovery;
    private final SoaDynamicAttributes dynamicAttributes;
    private final SoaInfo info;

    public SoaFeaturesImpl()
    {
        this(null, null, null);
    }

    SoaFeaturesImpl(SoaDiscovery discovery, SoaDynamicAttributes dynamicAttributes, SoaInfo info)
    {
        this.discovery = discovery;
        this.dynamicAttributes = dynamicAttributes;
        this.info = info;
    }

    void setNamed(SoaFeaturesImpl from)
    {
        this.named.clear();
        this.named.putAll(from.named);
    }

    @Override
    public SoaDiscovery getDiscovery()
    {
        return discovery;
    }

    @Override
    public SoaDynamicAttributes getAttributes()
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
