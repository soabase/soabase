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
package io.soabase.sql.attributes;

import com.google.common.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.InputStream;
import java.util.Collections;

public class TestSqlDynamicAttributes
{
    private SqlSession session;
    private SqlDynamicAttributes dynamicAttributes;

    @BeforeMethod
    public void setup() throws Exception
    {
        try ( InputStream stream = Resources.getResource("test-mybatis.xml").openStream() )
        {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
            Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
            mybatisConfiguration.addMapper(AttributeEntityMapper.class);
            session = sqlSessionFactory.openSession(true);

            AttributeEntityMapper mapper = session.getMapper(AttributeEntityMapper.class);
            mapper.createTable();

            dynamicAttributes = new SqlDynamicAttributes(session, Collections.singletonList("test"));
            dynamicAttributes.start();
        }
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        dynamicAttributes.stop();
        session.close();
    }

    @Test
    public void testBasic()
    {
        Assert.assertEquals(dynamicAttributes.getAttribute("file.separator"), System.getProperty("file.separator"));
        Assert.assertEquals(dynamicAttributes.getAttribute("os.version"), System.getProperty("os.version"));

        Assert.assertEquals(dynamicAttributes.getAttribute("test.foo.bar", ""), "");

        AttributeEntityMapper mapper = session.getMapper(AttributeEntityMapper.class);
        AttributeEntity attribute = new AttributeEntity("test.foo.bar", "test");
        mapper.insert(attribute);
        dynamicAttributes.update();
        Assert.assertEquals(dynamicAttributes.getAttribute("test.foo.bar", ""), "test");

        attribute = new AttributeEntity("test.foo.bar", "bad", "scoped-value");
        mapper.insert(attribute);
        dynamicAttributes.update();
        Assert.assertEquals(dynamicAttributes.getAttribute("test.foo.bar", ""), "test");

        attribute = new AttributeEntity("test.foo.bar", "test", "new-value");
        mapper.insert(attribute);
        dynamicAttributes.update();
        Assert.assertEquals(dynamicAttributes.getAttribute("test.foo.bar", ""), "new-value");
    }
}
