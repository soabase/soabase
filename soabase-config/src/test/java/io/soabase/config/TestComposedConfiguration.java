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
package io.soabase.config;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import io.soabase.config.mocks.ExtendedConfiguration;
import io.soabase.config.mocks.TestConfiguration1;
import io.soabase.config.mocks.TestConfiguration2;
import io.soabase.config.service.FromServices;
import org.testng.Assert;
import org.testng.annotations.Test;
import javax.validation.Validation;
import javax.validation.Validator;

public class TestComposedConfiguration
{
    @Test
    public void testGeneral() throws Exception
    {
        ComposedConfigurationBuilder<ComposedConfiguration> builder = new ComposedConfigurationBuilder<>("x.a", ComposedConfiguration.class);
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
        ComposedConfigurationBuilder<ComposedConfiguration> builder = new ComposedConfigurationBuilder<>("x.b", ComposedConfiguration.class);
        builder.add("t1", TestConfiguration1.class);
        builder.add("t2", TestConfiguration2.class);
        ComposedConfigurationFactoryFactory<ComposedConfiguration> factoryFactory = new ComposedConfigurationFactoryFactory<>(builder);

        internalTestViaFactoryFactory(factoryFactory);
    }

    @Test
    public void testViaServices() throws Exception
    {
        ConfigurationFactoryFactory<ComposedConfiguration> factoryFactory = FromServices
            .create()
            .withFqClassName("x.c")
            .withBaseClass(ComposedConfiguration.class)
            .factory();
        internalTestViaFactoryFactory(factoryFactory);
    }

    @Test
    public void testExtended() throws Exception
    {
        ComposedConfigurationBuilder<ExtendedConfiguration> builder = new ComposedConfigurationBuilder<>("x.d", ExtendedConfiguration.class);
        Class<ExtendedConfiguration> clazz = builder.build();
        ExtendedConfiguration configuration = clazz.newInstance();
        Assert.assertNotNull(configuration);
        Assert.assertEquals(configuration.getField1(), "1");
        Assert.assertEquals(configuration.getField2(), "2");
    }

    private void internalTestViaFactoryFactory(ConfigurationFactoryFactory<ComposedConfiguration> factoryFactory) throws java.io.IOException, io.dropwizard.configuration.ConfigurationException
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
