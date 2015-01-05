package io.soabase.core.features.config;

import io.soabase.core.features.config.mocks.BaseConfiguration;
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
}
