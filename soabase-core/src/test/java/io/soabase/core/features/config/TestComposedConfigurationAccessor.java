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
package io.soabase.core.features.config;

import io.soabase.core.features.config.mocks.BaseConfiguration;
import io.soabase.core.features.config.mocks.ConfigurationWithArray;
import io.soabase.core.features.config.mocks.ContainerConfiguration;
import io.soabase.core.features.config.mocks.DuplicateConfiguration;
import io.soabase.core.features.config.mocks.MyConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestComposedConfigurationAccessor
{
    @Test
    public void testBasic()
    {
        ComposedConfigurationAccessor accessor = new ComposedConfigurationAccessor(new ContainerConfiguration());
        MyConfiguration accessed = accessor.access(MyConfiguration.class);
        Assert.assertNotNull(accessed);
        Assert.assertEquals(accessed.testValue, "unset");
    }

    @Test
    public void testSubClass()
    {
        ComposedConfigurationAccessor accessor = new ComposedConfigurationAccessor(new BaseConfiguration());
        MyConfiguration accessed = accessor.access(MyConfiguration.class);
        Assert.assertNotNull(accessed);
        Assert.assertEquals(accessed.testValue, "unset");
    }

    @Test
    public void testIdentity()
    {
        MyConfiguration configuration = new MyConfiguration();
        ComposedConfigurationAccessor accessor = new ComposedConfigurationAccessor(configuration);
        MyConfiguration accessed = accessor.access(MyConfiguration.class);
        Assert.assertSame(configuration, accessed);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testFailure()
    {
        new ComposedConfigurationAccessor("").access(MyConfiguration.class);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testDuplicate()
    {
        new ComposedConfigurationAccessor(new DuplicateConfiguration()).access(MyConfiguration.class);
    }

    @Test
    public void testArray()
    {
        ComposedConfigurationAccessor accessor = new ComposedConfigurationAccessor(new ConfigurationWithArray());
        MyConfiguration[] configurations = accessor.access(MyConfiguration[].class);
        Assert.assertNotNull(configurations);
        Assert.assertEquals(configurations.length, 5);
    }
}
