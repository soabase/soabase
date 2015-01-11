package io.soabase.core.features.attributes;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Arrays;

public class TestStandardAttributesContainer
{
    @Test
    public void testBasic()
    {
        StandardAttributesContainer container = new StandardAttributesContainer();
        Assert.assertEquals(container.getAll().size(), 0);

        container.newUpdater().put("one", "", 1.2).commit();
        Assert.assertEquals(container.getAttribute("one", ""), "1.2");
        Assert.assertEquals(container.getAttributeInt("one", 0), 1);
        Assert.assertEquals(container.getAttributeLong("one", 0), 1L);
        Assert.assertEquals(container.getAttributeDouble("one", 0.0), 1.2);
        Assert.assertEquals(container.getAttributeBoolean("one", false), true);
    }

    @Test
    public void testScopes()
    {
        StandardAttributesContainer container = new StandardAttributesContainer(Arrays.asList("a", "b"));

        container.newUpdater()
            .put("one", "", 1)
            .put("one", "a", 2)
            .put("one", "b", 3)
            .commit();
        Assert.assertEquals(container.getAttributeInt("one", 0), 2);    // first matching scope wins

        container.newUpdater()
            .put("one", "", 1)
            .put("one", "b", 3)
            .commit();
        Assert.assertEquals(container.getAttributeInt("one", 0), 3);

        container.newUpdater()
            .put("one", "", 1)
            .commit();
        Assert.assertEquals(container.getAttributeInt("one", 0), 1);

        container.temporaryOverride("one", "over");
        Assert.assertEquals(container.getAttribute("one", ""), "over");
        Assert.assertEquals(container.getAttributeInt("one", 0), 0);

        container.newUpdater()
            .put("two", "", 1)
            .put("two", "y", 2)
            .put("two", "z", 3)
            .commit();
        Assert.assertEquals(container.getAttributeInt("two", 0), 1);    // other scopes don't match
    }
}
