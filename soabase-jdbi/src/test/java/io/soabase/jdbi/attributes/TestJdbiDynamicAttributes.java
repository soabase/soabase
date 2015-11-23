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
package io.soabase.jdbi.attributes;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import java.util.Collections;

public class TestJdbiDynamicAttributes
{
    private JdbiDynamicAttributes dynamicAttributes;

    @Before
    public void setup() throws Exception
    {
        DBIFactory factory = new DBIFactory();
        Environment environment = new Environment("test", new ObjectMapper(), null, new MetricRegistry(), ClassLoader.getSystemClassLoader());
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setUrl("jdbc:hsqldb:mem:soa-jdbi;shutdown=true");
        dataSourceFactory.setDriverClass("org.hsqldb.jdbc.JDBCDriver");
        dataSourceFactory.setLogValidationErrors(true);
        dataSourceFactory.setUser("SA");
        dataSourceFactory.setValidationQuery("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES");
        DBI jdbi = factory.build(environment, dataSourceFactory, "test");
        dynamicAttributes = new JdbiDynamicAttributes(jdbi, Collections.singletonList("test"));

        dynamicAttributes.getDao().createTable();
        dynamicAttributes.start();
    }

    @After
    public void tearDown() throws Exception
    {
        dynamicAttributes.stop();
    }

    @Test
    public void testBasic() throws Exception
    {
        Assert.assertEquals(System.getProperty("file.separator"), dynamicAttributes.getAttribute("file.separator"));
        Assert.assertEquals(System.getProperty("os.version"), dynamicAttributes.getAttribute("os.version"));

        Assert.assertEquals("", dynamicAttributes.getAttribute("test.foo.bar", ""));

        AttributeEntity attribute = new AttributeEntity("test.foo.bar", "test");
        dynamicAttributes.getDao().insert(attribute.getfKEY(), attribute.getfSCOPE(), attribute.getfVALUE(), attribute.getfTIMESTAMP());
        dynamicAttributes.update();
        Assert.assertEquals("test", dynamicAttributes.getAttribute("test.foo.bar", ""));

        attribute = new AttributeEntity("test.foo.bar", "bad", "scoped-value");
        dynamicAttributes.getDao().insert(attribute.getfKEY(), attribute.getfSCOPE(), attribute.getfVALUE(), attribute.getfTIMESTAMP());
        dynamicAttributes.update();
        Assert.assertEquals("test", dynamicAttributes.getAttribute("test.foo.bar", ""));

        attribute = new AttributeEntity("test.foo.bar", "test", "new-value");
        dynamicAttributes.getDao().insert(attribute.getfKEY(), attribute.getfSCOPE(), attribute.getfVALUE(), attribute.getfTIMESTAMP());
        dynamicAttributes.update();
        Assert.assertEquals("new-value", dynamicAttributes.getAttribute("test.foo.bar", ""));

        dynamicAttributes.getDao().delete("test.foo.bar", "test");
        dynamicAttributes.update();
        Assert.assertEquals("test", dynamicAttributes.getAttribute("test.foo.bar", ""));
    }
}
