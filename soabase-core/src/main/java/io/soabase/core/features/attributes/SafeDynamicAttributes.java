package io.soabase.core.features.attributes;

import io.soabase.core.listening.Listenable;
import io.soabase.core.rest.entities.Attribute;
import java.util.Collection;
import java.util.Iterator;

public class SafeDynamicAttributes implements SoaDynamicAttributes
{
    private final SoaDynamicAttributes implementation;

    public SafeDynamicAttributes(SoaDynamicAttributes implementation)
    {
        this.implementation = implementation;
    }

    @Override
    public String getAttribute(String key)
    {
        return implementation.getAttribute(key);
    }

    @Override
    public String getAttribute(String key, String defaultValue)
    {
        return implementation.getAttribute(key, defaultValue);
    }

    @Override
    public boolean getAttributeBoolean(String key)
    {
        return implementation.getAttributeBoolean(key);
    }

    @Override
    public boolean getAttributeBoolean(String key, boolean defaultValue)
    {
        return implementation.getAttributeBoolean(key, defaultValue);
    }

    @Override
    public int getAttributeInt(String key)
    {
        return implementation.getAttributeInt(key);
    }

    @Override
    public int getAttributeInt(String key, int defaultValue)
    {
        return implementation.getAttributeInt(key, defaultValue);
    }

    @Override
    public long getAttributeLong(String key)
    {
        return implementation.getAttributeLong(key);
    }

    @Override
    public long getAttributeLong(String key, long defaultValue)
    {
        return implementation.getAttributeLong(key, defaultValue);
    }

    @Override
    public void temporaryOverride(String key, boolean value)
    {
        implementation.temporaryOverride(key, value);
    }

    @Override
    public void temporaryOverride(String key, int value)
    {
        implementation.temporaryOverride(key, value);
    }

    @Override
    public void temporaryOverride(String key, long value)
    {
        implementation.temporaryOverride(key, value);
    }

    @Override
    public void temporaryOverride(String key, String value)
    {
        implementation.temporaryOverride(key, value);
    }

    @Override
    public boolean removeOverride(String key)
    {
        return implementation.removeOverride(key);
    }

    @Override
    public Collection<String> getKeys()
    {
        return implementation.getKeys();
    }

    @Override
    public Listenable<SoaDynamicAttributeListener> getListenable()
    {
        return implementation.getListenable();
    }

    @Override
    public Iterator<Attribute> iterator()
    {
        return implementation.iterator();
    }
}
