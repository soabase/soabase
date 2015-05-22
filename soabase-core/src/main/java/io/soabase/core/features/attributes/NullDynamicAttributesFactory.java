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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.soabase.core.listening.Listenable;
import java.util.Collection;
import java.util.List;

@JsonTypeName("default")
public class NullDynamicAttributesFactory implements DynamicAttributesFactory
{
    @Override
    public DynamicAttributes build(Configuration configuration, Environment environment, List<String> scopes)
    {
        return new NullAttributes();
    }

    private static class NullAttributes implements DynamicAttributes
    {
        private final StandardAttributesContainer container = new StandardAttributesContainer(Lists.<String>newArrayList());

        @Override
        public String getAttribute(String key)
        {
            return container.getAttribute(key, null);
        }

        @Override
        public String getAttribute(String key, String defaultValue)
        {
            return container.getAttribute(key, defaultValue);
        }

        @Override
        public boolean getAttributeBoolean(String key)
        {
            return container.getAttributeBoolean(key, false);
        }

        @Override
        public boolean getAttributeBoolean(String key, boolean defaultValue)
        {
            return container.getAttributeBoolean(key, defaultValue);
        }

        @Override
        public int getAttributeInt(String key)
        {
            return container.getAttributeInt(key, 0);
        }

        @Override
        public int getAttributeInt(String key, int defaultValue)
        {
            return container.getAttributeInt(key, defaultValue);
        }

        @Override
        public long getAttributeLong(String key)
        {
            return container.getAttributeLong(key, 0L);
        }

        @Override
        public long getAttributeLong(String key, long defaultValue)
        {
            return container.getAttributeLong(key, defaultValue);
        }

        @Override
        public double getAttributeDouble(String key)
        {
            return container.getAttributeDouble(key, 0.0);
        }

        @Override
        public double getAttributeDouble(String key, double defaultValue)
        {
            return container.getAttributeDouble(key, defaultValue);
        }

        @Override
        public void temporaryOverride(String key, boolean value)
        {
            container.temporaryOverride(key, value);
        }

        @Override
        public void temporaryOverride(String key, int value)
        {
            container.temporaryOverride(key, value);
        }

        @Override
        public void temporaryOverride(String key, long value)
        {
            container.temporaryOverride(key, value);
        }

        @Override
        public void temporaryOverride(String key, String value)
        {
            container.temporaryOverride(key, value);
        }

        @Override
        public void temporaryOverride(String key, double value)
        {
            container.temporaryOverride(key, value);
        }

        @Override
        public boolean removeOverride(String key)
        {
            return container.removeOverride(key);
        }

        @Override
        public Collection<String> getKeys()
        {
            return container.getKeys();
        }

        @Override
        public Listenable<DynamicAttributeListener> getListenable()
        {
            return container.getListenable();
        }
    }
}
