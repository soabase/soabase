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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestDynamicAttributeListener
{
    @Test
    public void testBasic() throws InterruptedException
    {
        StandardAttributesContainer container = new StandardAttributesContainer();

        final CountDownLatch attributeChangedLatch = new CountDownLatch(1);
        final CountDownLatch attributeAddedLatch = new CountDownLatch(1);
        final CountDownLatch attributeRemovedLatch = new CountDownLatch(1);
        DynamicAttributeListener listener = new DynamicAttributeListenerAdapter()
        {
            @Override
            public void attributeChanged(String key, String scope)
            {
                attributeChangedLatch.countDown();
            }

            @Override
            public void attributeAdded(String key, String scope)
            {
                attributeAddedLatch.countDown();
            }

            @Override
            public void attributeRemoved(String key, String scope)
            {
                attributeRemovedLatch.countDown();
            }
        };
        container.getListenable().addListener(listener);

        container.reset(container.getAll());    // first commit doesn't notify
        Assert.assertEquals(attributeChangedLatch.getCount(), 1);
        Assert.assertEquals(attributeAddedLatch.getCount(), 1);
        Assert.assertEquals(attributeRemovedLatch.getCount(), 1);

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("a", "one"), "a");
        container.reset(newAttributes);
        Assert.assertTrue(attributeAddedLatch.await(1, TimeUnit.MILLISECONDS));
        Assert.assertEquals(attributeChangedLatch.getCount(), 1);
        Assert.assertEquals(attributeRemovedLatch.getCount(), 1);

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("a", "one"), "b");
        container.reset(newAttributes);
        Assert.assertTrue(attributeChangedLatch.await(1, TimeUnit.MILLISECONDS));
        Assert.assertEquals(attributeRemovedLatch.getCount(), 1);

        newAttributes = Maps.newHashMap();
        container.reset(newAttributes);
        Assert.assertTrue(attributeRemovedLatch.await(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testOverrides() throws InterruptedException
    {
        StandardAttributesContainer container = new StandardAttributesContainer();

        final CountDownLatch overrideAddedLatch = new CountDownLatch(1);
        final CountDownLatch overrideRemovedLatch = new CountDownLatch(1);
        DynamicAttributeListener listener = new DynamicAttributeListenerAdapter()
        {
            @Override
            public void overrideAdded(String key)
            {
                overrideAddedLatch.countDown();
            }

            @Override
            public void overrideRemoved(String key)
            {
                overrideRemovedLatch.countDown();
            }
        };
        container.getListenable().addListener(listener);

        container.temporaryOverride("a", "a");
        Assert.assertTrue(overrideAddedLatch.await(1, TimeUnit.MILLISECONDS));
        Assert.assertEquals(overrideRemovedLatch.getCount(), 1);

        container.removeOverride("a");
        Assert.assertTrue(overrideRemovedLatch.await(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testScopes()
    {
        StandardAttributesContainer container = new StandardAttributesContainer();
        RecordingListener listener = new RecordingListener();
        container.getListenable().addListener(listener);

        Map<AttributeKey, Object> newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("a", "a"), "a");
        newAttributes.put(new AttributeKey("a", "b"), "a");
        newAttributes.put(new AttributeKey("b", "a"), "b");
        newAttributes.put(new AttributeKey("b", "b"), "b");
        container.reset(newAttributes);    // first commit doesn't notify

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("a", "a"), "new");
        newAttributes.put(new AttributeKey("a", "c"), "first");
        newAttributes.put(new AttributeKey("b", "a"), "one");
        newAttributes.put(new AttributeKey("b", "b"), "two");
        newAttributes.put(new AttributeKey("b", ""), "hey");
        container.reset(newAttributes);

        Set<ListenerEntry> expected = Sets.newHashSet
            (
                new ListenerEntry("attributeChanged", "a", "a"),
                new ListenerEntry("attributeAdded", "a", "c"),
                new ListenerEntry("attributeChanged", "b", "a"),
                new ListenerEntry("attributeChanged", "b", "b"),
                new ListenerEntry("attributeAdded", "b", ""),
                new ListenerEntry("attributeRemoved", "a", "b")
            );
        Assert.assertEquals(Sets.newHashSet(listener.getEntries()), expected);
        listener.clear();

        newAttributes = Maps.newHashMap();
        newAttributes.put(new AttributeKey("a", "a"), "new");
        newAttributes.put(new AttributeKey("a", "c"), "first");
        newAttributes.put(new AttributeKey("b", "a"), "one");
        newAttributes.put(new AttributeKey("b", "b"), "two");
        newAttributes.put(new AttributeKey("b", ""), "hey");
        container.reset(newAttributes);
        Assert.assertEquals(listener.getEntries().size(), 0);
    }
}
