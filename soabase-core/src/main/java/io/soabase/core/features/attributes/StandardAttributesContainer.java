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
        this(Lists.<String>newArrayList());
    }

    public StandardAttributesContainer(List<String> scopes)
    {
        scopes = Preconditions.checkNotNull(scopes, "scopes cannot be null");
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.addAll(scopes);
        builder.add(DEFAULT_SCOPE);
        this.scopes = builder.build();
    }

    public interface Updater
    {
        public Updater put(String key, String scope, Object value);

        public void commit();
    }

    public Updater newUpdater()
    {
        final Set<AttributeKey> deletingKeys = Sets.newHashSet(attributes.keySet());
        final boolean notifyListeners = !firstTime.compareAndSet(true, false);
        return new Updater()
        {
            @Override
            public Updater put(final String key, final String scope, Object value)
            {
                AttributeKey attributeKey = new AttributeKey(key, scope);
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
                                listener.attributeAdded(key, scope);
                            }
                            else
                            {
                                listener.attributeChanged(key, scope);
                            }
                            return null;
                        }
                    };
                    if ( notifyListeners )
                    {
                        listenable.forEach(notify);
                    }
                }
                return this;
            }

            @Override
            public void commit()
            {
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
        };
    }

    public String getAttribute(String key, String defaultValue)
    {
        Object value = overrides.get(key);
        if ( value == null )
        {
            value = System.getProperty(key, null);
            if ( value == null )
            {
                value = getValue(key);
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
        return overrides.keySet();
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
            value = System.getProperty(key, null);
        }
        return to(value, null);
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
        return Long.parseLong(String.valueOf(value));
    }
}
