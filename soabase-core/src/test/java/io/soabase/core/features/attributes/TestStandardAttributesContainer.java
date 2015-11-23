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
        Assert.assertEquals(0, container.getAll().size());

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1.2);
        container.reset(newAttributes);

        Assert.assertEquals("1.2", container.getAttribute("one", ""));
        Assert.assertEquals(1, container.getAttributeInt("one", 0));
        Assert.assertEquals(1L, container.getAttributeLong("one", 0));
        Assert.assertEquals(1.2, container.getAttributeDouble("one", 0.0), 0);
        Assert.assertEquals(true, container.getAttributeBoolean("one", false));
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
        Assert.assertEquals(2, container.getAttributeInt("one", 0));    // first matching scope wins

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1);
        newAttributes.put(new AttributeKey("one", "b"), 3);
        container.reset(newAttributes);
        Assert.assertEquals(3, container.getAttributeInt("one", 0));

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("one", ""), 1);
        container.reset(newAttributes);
        Assert.assertEquals(1, container.getAttributeInt("one", 0));

        container.temporaryOverride("one", "over");
        Assert.assertEquals("over", container.getAttribute("one", ""));
        Assert.assertEquals(0, container.getAttributeInt("one", 0));

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("two", ""), 1);
        newAttributes.put(new AttributeKey("two", "y"), 2);
        newAttributes.put(new AttributeKey("two", "z"), 3);
        container.reset(newAttributes);
        Assert.assertEquals(1, container.getAttributeInt("two", 0));    // other scopes don't match
    }

    @Test
    public void testOverrideOrder()
    {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("one", "1");
        defaultProperties.setProperty("two", "2");
        StandardAttributesContainer container = new StandardAttributesContainer(defaultProperties, Collections.<String>emptyList());

        Assert.assertEquals("1", container.getAttribute("one", null));
        Assert.assertEquals("2", container.getAttribute("two", null));
        Assert.assertNull(container.getAttribute("three", null));

        container.temporaryOverride("two", "too");
        Assert.assertEquals("1", container.getAttribute("one", null));
        Assert.assertEquals("too", container.getAttribute("two", null));
        Assert.assertNull(container.getAttribute("three", null));
        container.removeOverride("two");

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("two", ""), "new");
        container.reset(newAttributes);
        Assert.assertEquals("1", container.getAttribute("one", null));
        Assert.assertEquals("new", container.getAttribute("two", null));
        Assert.assertNull(container.getAttribute("three", null));
    }
}
