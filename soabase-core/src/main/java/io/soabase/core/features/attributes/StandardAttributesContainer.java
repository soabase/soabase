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
package io.soabase.core.features.attributes;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.soabase.core.listening.Listenable;
import io.soabase.core.listening.ListenerContainer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class StandardAttributesContainer
{
    public static final String DEFAULT_SCOPE = "";

    private final Map<String, Object> overrides = Maps.newConcurrentMap();
    private final Map<AttributeKey, Object> attributes = Maps.newConcurrentMap();
    private final ListenerContainer<DynamicAttributeListener> listenable = new ListenerContainer<>();
    private final List<String> scopes;
    private final AtomicBoolean firstTime = new AtomicBoolean(true);
    private final DefaultPropertiesAccessor defaultProperties;

    public static DynamicAttributes wrapAttributes(DynamicAttributes attributes, boolean hasAdminKey)
    {
        if ( (attributes instanceof WritableDynamicAttributes) && !hasAdminKey )
        {
            return new SafeDynamicAttributes(attributes);
        }
        return attributes;
    }

    public StandardAttributesContainer()
    {
        this(new Accessor(System.getProperties()), Lists.<String>newArrayList());
    }

    public StandardAttributesContainer(List<String> scopes)
    {
        this(new Accessor(System.getProperties()), scopes);
    }

    public StandardAttributesContainer(Properties defaultProperties, List<String> scopes)
    {
        this(new Accessor(defaultProperties), scopes);
    }

    public StandardAttributesContainer(DefaultPropertiesAccessor defaultProperties, List<String> scopes)
    {
        this.defaultProperties = Preconditions.checkNotNull(defaultProperties, "defaultProperties cannot be null");
        scopes = Preconditions.checkNotNull(scopes, "scopes cannot be null");
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.addAll(scopes);
        builder.add(DEFAULT_SCOPE);
        this.scopes = builder.build();
    }

    public void reset(Map<AttributeKey, Object> newAttributes)
    {
        final Set<AttributeKey> deletingKeys = Sets.newHashSet(attributes.keySet());
        final boolean notifyListeners = !firstTime.compareAndSet(true, false);

        for ( Map.Entry<AttributeKey, Object> entry : newAttributes.entrySet() )
        {
            final AttributeKey attributeKey = entry.getKey();
            Object value = entry.getValue();
            deletingKeys.remove(attributeKey);

            final boolean isNew = !attributes.containsKey(attributeKey);
            if ( isNew || !Objects.equal(value, attributes.get(attributeKey)) )
            {
                attributes.put(attributeKey, value);

                Function<DynamicAttributeListener, Void> notify = new Function<DynamicAttributeListener, Void>()
                {
                    @Override
                    public Void apply(DynamicAttributeListener listener)
                    {
                        if ( isNew )
                        {
                            listener.attributeAdded(attributeKey.getKey(), attributeKey.getScope());
                        }
                        else
                        {
                            listener.attributeChanged(attributeKey.getKey(), attributeKey.getScope());
                        }
                        return null;
                    }
                };
                if ( notifyListeners )
                {
                    listenable.forEach(notify);
                }
            }
        }

        for ( final AttributeKey attributeKey : deletingKeys )
        {
            attributes.remove(attributeKey);
            if ( notifyListeners )
            {
                Function<DynamicAttributeListener, Void> notify = new Function<DynamicAttributeListener, Void>()
                {
                    @Override
                    public Void apply(DynamicAttributeListener listener)
                    {
                        listener.attributeRemoved(attributeKey.getKey(), attributeKey.getScope());
                        return null;
                    }
                };
                listenable.forEach(notify);
            }
        }
    }

    public String getAttribute(String key, String defaultValue)
    {
        Object value = overrides.get(key);
        if ( value == null )
        {
            value = getValue(key);
            if ( value == null )
            {
                value = defaultProperties.getProperty(key, null);
            }
        }
        return (value != null) ? value.toString() : defaultValue;
    }

    public boolean getAttributeBoolean(String key, boolean defaultValue)
    {
        Number value = getOverrideNumber(key);
        if ( value != null )
        {
            return value.intValue() != 0;
        }

        return to(getValue(key), defaultValue ? 1 : 0).intValue() != 0;
    }

    public int getAttributeInt(String key, int defaultValue)
    {
        Number value = getOverrideNumber(key);
        if ( value != null )
        {
            return value.intValue();
        }

        return to(getValue(key), defaultValue).intValue();
    }

    public long getAttributeLong(String key, long defaultValue)
    {
        Number value = getOverrideNumber(key);
        if ( value != null )
        {
            return value.longValue();
        }

        return to(getValue(key), defaultValue).longValue();
    }

    public double getAttributeDouble(String key, double defaultValue)
    {
        Number value = getOverrideNumber(key);
        if ( value != null )
        {
            return value.doubleValue();
        }

        return to(getValue(key), defaultValue).doubleValue();
    }

    public void temporaryOverride(String key, boolean value)
    {
        internalTemporaryOverride(key, value);
    }

    public void temporaryOverride(String key, int value)
    {
        internalTemporaryOverride(key, value);
    }

    public void temporaryOverride(String key, long value)
    {
        internalTemporaryOverride(key, value);
    }

    public void temporaryOverride(String key, double value)
    {
        internalTemporaryOverride(key, value);
    }

    public void temporaryOverride(String key, String value)
    {
        internalTemporaryOverride(key, value);
    }

    public boolean removeOverride(final String key)
    {
        boolean hadOverride = (overrides.remove(key) != null);
        if ( hadOverride )
        {
            Function<DynamicAttributeListener, Void> notify = new Function<DynamicAttributeListener, Void>()
            {
                @Override
                public Void apply(DynamicAttributeListener listener)
                {
                    listener.overrideRemoved(key);
                    return null;
                }
            };
            listenable.forEach(notify);
        }
        return hadOverride;
    }

    public Collection<String> getKeys()
    {
        Set<String> keys = Sets.newHashSet();

        for ( Object key : defaultProperties.keySet() )
        {
            keys.add(String.valueOf(key));
        }
        for ( AttributeKey key : attributes.keySet() )
        {
            keys.add(key.getKey());
        }
        keys.addAll(overrides.keySet());
        return keys;
    }

    public boolean hasKey(AttributeKey key)
    {
        return attributes.containsKey(key);
    }

    public Map<AttributeKey, Object> getAll()
    {
        return Maps.newHashMap(attributes);
    }

    public Listenable<DynamicAttributeListener> getListenable()
    {
        return listenable;
    }

    private void internalTemporaryOverride(final String key, Object value)
    {
        overrides.put(key, value);
        Function<DynamicAttributeListener, Void> notify = new Function<DynamicAttributeListener, Void>()
        {
            @Override
            public Void apply(DynamicAttributeListener listener)
            {
                listener.overrideAdded(key);
                return null;
            }
        };
        listenable.forEach(notify);
    }

    private Object getValue(String key)
    {
        for ( String scope : scopes )
        {
            Object value = attributes.get(new AttributeKey(key, scope));
            if ( value != null )
            {
                return value;
            }
        }

        return null;
    }

    private Number getOverrideNumber(String key)
    {
        Object value = overrides.get(key);
        if ( value == null )
        {
            value = defaultProperties.getProperty(key, null);
        }
        return (value != null) ? to(value, 0) : null;
    }

    private static Number to(Object value, Number defaultValue)
    {
        if ( value == null )
        {
            return defaultValue;
        }
        if ( value instanceof Number )
        {
            return (Number)value;
        }
        try
        {
            return Long.parseLong(String.valueOf(value));
        }
        catch ( NumberFormatException e )
        {
            // ignore
        }
        return defaultValue;
    }

    private static class Accessor implements DefaultPropertiesAccessor
    {
        private final Properties properties;

        Accessor(Properties properties)
        {
            this.properties = properties;
        }

        @Override
        public String getProperty(String key, String defaultValue)
        {
            return properties.getProperty(key, defaultValue);
        }

        @Override
        public Set<?> keySet()
        {
            return properties.keySet();
        }
    }
}
