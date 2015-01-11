package io.soabase.core.features.attributes;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
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

        container.newUpdater().complete();    // first commit doesn't notify
        Assert.assertEquals(attributeChangedLatch.getCount(), 1);
        Assert.assertEquals(attributeAddedLatch.getCount(), 1);
        Assert.assertEquals(attributeRemovedLatch.getCount(), 1);

        container.newUpdater().resetAttribute("a", "one", "a").complete();
        Assert.assertTrue(attributeAddedLatch.await(1, TimeUnit.MILLISECONDS));
        Assert.assertEquals(attributeChangedLatch.getCount(), 1);
        Assert.assertEquals(attributeRemovedLatch.getCount(), 1);

        container.newUpdater().resetAttribute("a", "one", "b").complete();
        Assert.assertTrue(attributeChangedLatch.await(1, TimeUnit.MILLISECONDS));
        Assert.assertEquals(attributeRemovedLatch.getCount(), 1);

        container.newUpdater().complete();
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

        container.newUpdater()
            .resetAttribute("a", "a", "a")
            .resetAttribute("a", "b", "a")
            .resetAttribute("b", "a", "b")
            .resetAttribute("b", "b", "b")
            .complete();    // first commit doesn't notify
        Assert.assertEquals(listener.getEntries().size(), 0);

        container.newUpdater()
            .resetAttribute("a", "a", "new")
            .resetAttribute("a", "c", "first")
            .resetAttribute("b", "a", "one")
            .resetAttribute("b", "b", "two")
            .resetAttribute("b", "", "hey")
            .complete();

        List<ListenerEntry> expected = Lists.newArrayList
        (
            new ListenerEntry("attributeChanged", "a", "a"),
            new ListenerEntry("attributeAdded", "a", "c"),
            new ListenerEntry("attributeChanged", "b", "a"),
            new ListenerEntry("attributeChanged", "b", "b"),
            new ListenerEntry("attributeAdded", "b", ""),
            new ListenerEntry("attributeRemoved", "a", "b")
        );
        Assert.assertEquals(listener.getEntries(), expected);
        listener.clear();

        container.newUpdater()
            .resetAttribute("a", "a", "new")
            .resetAttribute("a", "c", "first")
            .resetAttribute("b", "a", "one")
            .resetAttribute("b", "b", "two")
            .resetAttribute("b", "", "hey")
            .complete();
        Assert.assertEquals(listener.getEntries().size(), 0);
    }
}
