package io.soabase.config;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.soabase.config.mocks.TestConfiguration1;
import io.soabase.config.mocks.TestConfiguration2;
import org.testng.Assert;
import org.testng.annotations.Test;
import javax.validation.Validation;
import javax.validation.Validator;

public class TestComposedConfiguration
{
    @Test
    public void testGeneral() throws Exception
    {
        ComposedConfigurationBuilder builder = new ComposedConfigurationBuilder();
        builder.add("t1", TestConfiguration1.class);
        builder.add("t2", TestConfiguration2.class);
        Class<? extends ComposedConfiguration> build = builder.build();
        ComposedConfiguration configuration = build.newInstance();
        TestConfiguration1 c1 = configuration.as(TestConfiguration1.class);
        TestConfiguration2 c2 = configuration.as(TestConfiguration2.class);
        Assert.assertNotNull(c1);
        Assert.assertNotNull(c2);
    }

    @Test
    public void testViaDW() throws Exception
    {
        ComposedConfigurationBuilder builder = new ComposedConfigurationBuilder();
        builder.add("t1", TestConfiguration1.class);
        builder.add("t2", TestConfiguration2.class);
        ComposedConfigurationFactoryFactory factoryFactory = new ComposedConfigurationFactoryFactory(builder);

        internalTestViaFactoryFactory(factoryFactory);
    }

    @Test
    public void testViaServices() throws Exception
    {
        ComposedConfigurationFactoryFactory factoryFactory = ComposedConfigurationFactoryFactory.buildFromServices();
        internalTestViaFactoryFactory(factoryFactory);
    }

    private void internalTestViaFactoryFactory(ComposedConfigurationFactoryFactory factoryFactory) throws java.io.IOException, io.dropwizard.configuration.ConfigurationException
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ConfigurationFactory<ComposedConfiguration> factory = factoryFactory.create(ComposedConfiguration.class, validator, Jackson.newObjectMapper(), "dw");
        System.setProperty("dw.t1.two", "override two");
        System.setProperty("dw.t2.a", "override a");
        ComposedConfiguration configuration = factory.build();
        TestConfiguration1 c1 = configuration.as(TestConfiguration1.class);
        TestConfiguration2 c2 = configuration.as(TestConfiguration2.class);
        Assert.assertNotNull(c1);
        Assert.assertNotNull(c2);
        Assert.assertEquals(c1.getField1(), "1");
        Assert.assertEquals(c1.getField2(), "override two");
        Assert.assertEquals(c2.getField1(), "override a");
        Assert.assertEquals(c2.getField2(), "b");
    }
}
