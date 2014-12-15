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
package io.soabase.core;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.sql.attributes.AttributeEntity;
import io.soabase.sql.attributes.AttributeEntityMapper;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.sql.attributes.SqlConfiguration;
import org.apache.ibatis.session.SqlSession;

public class TestApplication extends Application<TestConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        System.setProperty("dw.soa.attributes.type", "sql");
        System.setProperty("dw.sql.mybatisConfigUrl", "test-mybatis.xml");
        new TestApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap)
    {
        ConfigurationAccessor<TestConfiguration, SoaConfiguration> soaAccessor = new ConfigurationAccessor<TestConfiguration, SoaConfiguration>()
        {
            @Override
            public SoaConfiguration accessConfiguration(TestConfiguration configuration)
            {
                return configuration.getSoaConfiguration();
            }
        };
        ConfigurationAccessor<TestConfiguration, SqlConfiguration> sqlAccessor = new ConfigurationAccessor<TestConfiguration, SqlConfiguration>()
        {
            @Override
            public SqlConfiguration accessConfiguration(TestConfiguration configuration)
            {
                return configuration.getSqlConfiguration();
            }
        };
        bootstrap.addBundle(new SqlBundle<>(soaAccessor, sqlAccessor));
        bootstrap.addBundle(new SoaBundle<>(soaAccessor));
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception
    {
        SqlSession sqlSession = configuration.getSoaConfiguration().getNamedRequired(SqlSession.class, SoaFeatures.DEFAULT_NAME);
        AttributeEntityMapper mapper = sqlSession.getMapper(AttributeEntityMapper.class);
        mapper.createTable();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
    }
}
