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
package io.soabase.example;

import com.google.common.io.Resources;
import io.soabase.sql.attributes.AttributeEntity;
import io.soabase.sql.attributes.AttributeEntityMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hsqldb.Server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class MockDatabase
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        if ( !Boolean.getBoolean("debug") )
        {
            OutputStream nullOut = new OutputStream()
            {
                @Override
                public void write(int b) throws IOException
                {
                }
            };
            System.setOut(new PrintStream(nullOut));
        }

        args = new String[]
            {
                "--database.0",
                "mem:test",
                "--dbname.0",
                "xdb",
                "--port",
                "10064"
            };
        Server.main(args);

        SqlSession session;
        try (InputStream stream = Resources.getResource("example-mybatis.xml").openStream())
        {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
            Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
            mybatisConfiguration.addMapper(AttributeEntityMapper.class);
            session = sqlSessionFactory.openSession(true);
        }

        AttributeEntityMapper mapper = session.getMapper(AttributeEntityMapper.class);
        mapper.createTable();

        mapper.insert(new AttributeEntity("test", "global"));
        mapper.insert(new AttributeEntity("service", "goodbye", "goodbye"));
        mapper.insert(new AttributeEntity("service", "hello", "hello"));

        List<AttributeEntity> attributeEntities = mapper.selectAll();
        System.out.println(attributeEntities);

        System.out.println("Running...");
        Thread.currentThread().join();
    }
}
