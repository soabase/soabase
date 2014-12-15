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
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.CheckedConfigurationAccessor;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaConfiguration;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;

public class SqlBundle<T extends io.dropwizard.Configuration> implements ConfiguredBundle<T>
{
    private final ConfigurationAccessor<T, SqlConfiguration> sqlAccessor;
    private final ConfigurationAccessor<T, SoaConfiguration> soaAccessor;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public SqlBundle(ConfigurationAccessor<T, SoaConfiguration> soaAccessor, ConfigurationAccessor<T, SqlConfiguration> sqlAccessor)
    {
        this.soaAccessor = new CheckedConfigurationAccessor<>(soaAccessor);
        this.sqlAccessor = new CheckedConfigurationAccessor<>(sqlAccessor);
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        SqlConfiguration sqlConfiguration = sqlAccessor.accessConfiguration(configuration);
        try
        {
            try ( InputStream stream = Resources.getResource(sqlConfiguration.getMybatisConfigUrl()).openStream() )
            {
                SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
                Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
                mybatisConfiguration.addMapper(AttributeEntityMapper.class);
                final SqlSession session = sqlSessionFactory.openSession();

                SoaConfiguration soaConfiguration = soaAccessor.accessConfiguration(configuration);
                soaConfiguration.putNamed(session, SqlSession.class, sqlConfiguration.getSessionName());
                Managed managed = new Managed()
                {
                    @Override
                    public void start() throws Exception
                    {

                    }

                    @Override
                    public void stop() throws Exception
                    {
                        session.close();
                    }
                };
                environment.lifecycle().manage(managed);
            }
        }
        catch ( Exception e )
        {
            // TODO logging
            log.error("Could not initialize MyBatis", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }
}
