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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import org.testng.Assert;
import org.testng.annotations.Test;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.nio.charset.Charset;

public class TestFlexibleConfigurationSourceProvider
{
    @Test
    public void testString() throws Exception
    {
        FlexibleConfigurationSourceProvider provider = new FlexibleConfigurationSourceProvider();
        Assert.assertEquals(ByteStreams.toByteArray(provider.open("%one")), "one".getBytes());
    }

    @Test
    public void testResource() throws Exception
    {
        FlexibleConfigurationSourceProvider provider = new FlexibleConfigurationSourceProvider();
        byte[] bytes = ByteStreams.toByteArray(provider.open("|package/test-file.txt"));
        Assert.assertEquals(bytes, "one\ntwo\nthree\n".getBytes());
    }

    @Test
    public void testMissingOverride() throws Exception
    {
        FlexibleConfigurationSourceProvider provider = new FlexibleConfigurationSourceProvider();
        byte[] bytes = ByteStreams.toByteArray(provider.open("foo/bad/bar/nada/nothing|package/test-file.txt"));
        Assert.assertEquals(bytes, "one\ntwo\nthree\n".getBytes());
    }

    @Test
    public void testOverride() throws Exception
    {
        File tempFile = File.createTempFile("temp", "temp");
        try
        {
            Files.write("override", tempFile, Charset.defaultCharset());

            FlexibleConfigurationSourceProvider provider = new FlexibleConfigurationSourceProvider();
            byte[] bytes = ByteStreams.toByteArray(provider.open(tempFile.getCanonicalPath() + "|package/test-file.txt"));
            Assert.assertEquals(bytes, "override".getBytes());
        }
        finally
        {
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }
    }

    public static class TestConfiguration extends Configuration
    {
        public String testValue = "unset";
    }

    @Test
    public void testViaDW() throws Exception
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        ConfigurationFactory<TestConfiguration> configurationFactory = new DefaultConfigurationFactoryFactory<TestConfiguration>().create(TestConfiguration.class, validator, objectMapper, "dw");
        TestConfiguration configuration = configurationFactory.build(new FlexibleConfigurationSourceProvider(), "%{\"testValue\": \"override\"}");
        Assert.assertEquals(configuration.testValue, "override");
    }
}
