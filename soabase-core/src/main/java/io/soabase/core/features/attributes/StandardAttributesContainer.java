package io.soabase.core.features.attributes;

import com.google.common.collect.Maps;
import io.soabase.core.listening.Listenable;
import io.soabase.core.listening.ListenerContainer;
import java.util.Collection;
import java.util.Map;

public class StandardAttributesContainer
{
    private final Map<String, Object> overrides = Maps.newConcurrentMap();
    private final Map<AttributeKey, Object> attributes = Maps.newConcurrentMap();
    private final ListenerContainer<SoaDynamicAttributeListener> listenable = new ListenerContainer<SoaDynamicAttributeListener>();
    private final String groupName;
    private final String instanceName;

    public StandardAttributesContainer(String groupName, String instanceName)
    {
        this.groupName = groupName;
        this.instanceName = instanceName;
    }

    public String getAttribute(String key, String defaultValue)
    {
        Object value = overrides.get(key);
        if ( value == null )
        {
            value = getValue(key);
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
        overrides.put(key, value);
    }

    public void temporaryOverride(String key, int value)
    {
        overrides.put(key, value);
    }

    public void temporaryOverride(String key, long value)
    {
        overrides.put(key, value);
    }

    public void temporaryOverride(String key, String value)
    {
        overrides.put(key, value);
    }

    public boolean removeOverride(String key)
    {
        return overrides.remove(key) != null;
    }

    public Collection<String> getKeys()
    {
        return overrides.keySet();
    }

    public Listenable<SoaDynamicAttributeListener> getListenable()
    {
        return listenable;
    }

    private Object getValue(String key)
    {
        Object value = attributes.get(new AttributeKey(key, groupName, instanceName));
        if ( value == null )
        {
            value = attributes.get(new AttributeKey(key, groupName, null));
            if ( value == null )
            {
                value = attributes.get(new AttributeKey(key, null, null));
            }
        }
        return value;
    }

    private Number getOverrideNumber(String key)
    {
        Object value = overrides.get(key);
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
