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

import io.soabase.core.listening.Listenable;
import java.util.Collection;

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
}
