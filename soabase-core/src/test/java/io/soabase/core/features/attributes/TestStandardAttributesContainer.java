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

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class TestStandardAttributesContainer
{
    @Test
    public void testBasic()
    {
        StandardAttributesContainer container = new StandardAttributesContainer();
        Assert.assertEquals(container.getAll().size(), 0);

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1.2);
        container.reset(newAttributes);

        Assert.assertEquals(container.getAttribute("one", ""), "1.2");
        Assert.assertEquals(container.getAttributeInt("one", 0), 1);
        Assert.assertEquals(container.getAttributeLong("one", 0), 1L);
        Assert.assertEquals(container.getAttributeDouble("one", 0.0), 1.2, 0);
        Assert.assertEquals(container.getAttributeBoolean("one", false), true);
    }

    @Test
    public void testScopes()
    {
        StandardAttributesContainer container = new StandardAttributesContainer(Arrays.asList("a", "b"));

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1);
        newAttributes.put(new AttributeKey("one", "a"), 2);
        newAttributes.put(new AttributeKey("one", "b"), 3);
        container.reset(newAttributes);
        Assert.assertEquals(container.getAttributeInt("one", 0), 2);    // first matching scope wins

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1);
        newAttributes.put(new AttributeKey("one", "b"), 3);
        container.reset(newAttributes);
        Assert.assertEquals(container.getAttributeInt("one", 0), 3);

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1);
        container.reset(newAttributes);
        Assert.assertEquals(container.getAttributeInt("one", 0), 1);

        container.temporaryOverride("one", "over");
        Assert.assertEquals(container.getAttribute("one", ""), "over");
        Assert.assertEquals(container.getAttributeInt("one", 0), 0);

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("two", ""), 1);
        newAttributes.put(new AttributeKey("two", "y"), 2);
        newAttributes.put(new AttributeKey("two", "z"), 3);
        container.reset(newAttributes);
        Assert.assertEquals(container.getAttributeInt("two", 0), 1);    // other scopes don't match
    }

    @Test
    public void testOverrideOrder()
    {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("one", "1");
        defaultProperties.setProperty("two", "2");
        StandardAttributesContainer container = new StandardAttributesContainer(defaultProperties, Collections.<String>emptyList());

        Assert.assertEquals(container.getAttribute("one", null), "1");
        Assert.assertEquals(container.getAttribute("two", null), "2");
        Assert.assertEquals(container.getAttribute("three", null), null);

        container.temporaryOverride("two", "too");
        Assert.assertEquals(container.getAttribute("one", null), "1");
        Assert.assertEquals(container.getAttribute("two", null), "too");
        Assert.assertEquals(container.getAttribute("three", null), null);
        container.removeOverride("two");

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("two", ""), "new");
        container.reset(newAttributes);
        Assert.assertEquals(container.getAttribute("one", null), "1");
        Assert.assertEquals(container.getAttribute("two", null), "new");
        Assert.assertEquals(container.getAttribute("three", null), null);
    }
}
