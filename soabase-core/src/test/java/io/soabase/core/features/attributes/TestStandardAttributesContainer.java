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

        container.newUpdater().resetAttribute("one", "", 1.2).complete();
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
            .resetAttribute("one", "", 1)
            .resetAttribute("one", "a", 2)
            .resetAttribute("one", "b", 3)
            .complete();
        Assert.assertEquals(container.getAttributeInt("one", 0), 2);    // first matching scope wins

        container.newUpdater()
            .resetAttribute("one", "", 1)
            .resetAttribute("one", "b", 3)
            .complete();
        Assert.assertEquals(container.getAttributeInt("one", 0), 3);

        container.newUpdater()
            .resetAttribute("one", "", 1)
            .complete();
        Assert.assertEquals(container.getAttributeInt("one", 0), 1);

        container.temporaryOverride("one", "over");
        Assert.assertEquals(container.getAttribute("one", ""), "over");
        Assert.assertEquals(container.getAttributeInt("one", 0), 0);

        container.newUpdater()
            .resetAttribute("two", "", 1)
            .resetAttribute("two", "y", 2)
            .resetAttribute("two", "z", 3)
            .complete();
        Assert.assertEquals(container.getAttributeInt("two", 0), 1);    // other scopes don't match
    }
}
