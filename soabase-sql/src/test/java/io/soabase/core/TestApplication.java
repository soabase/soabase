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
import io.soabase.core.config.ComposedConfiguration;
import io.soabase.sql.attributes.AttributeEntity;
import io.soabase.sql.attributes.AttributeEntityMapper;
import io.soabase.sql.attributes.SqlBundle;
import org.apache.ibatis.session.SqlSession;

public class TestApplication extends Application<ComposedConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        System.setProperty("dw.soa.attributes.type", "sql");
        System.setProperty("dw.sql.mybatisConfigUrl", "test-mybatis.xml");
        new TestApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<ComposedConfiguration> bootstrap)
    {
        bootstrap.addBundle(new SqlBundle());
        bootstrap.addBundle(new SoaBundle());
    }

    @Override
    public void run(ComposedConfiguration configuration, Environment environment) throws Exception
    {
        SoaConfiguration soaConfiguration = configuration.access(SoaBundle.CONFIGURATION_NAME, SoaConfiguration.class);
        SqlSession sqlSession = soaConfiguration.getNamedRequired(SqlSession.class, SoaFeatures.DEFAULT_NAME);
        AttributeEntityMapper mapper = sqlSession.getMapper(AttributeEntityMapper.class);
        mapper.createTable();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
    }
}
