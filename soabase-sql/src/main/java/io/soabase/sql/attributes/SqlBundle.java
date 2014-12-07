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
import java.io.IOException;
import java.io.InputStream;

public class SqlBundle<T extends io.dropwizard.Configuration> implements ConfiguredBundle<T>
{
    private final ConfigurationAccessor<T> accessor;

    public static SqlSession getSqlSession(SoaConfiguration configuration)
    {
        return configuration.getNamed(SqlSession.class, SqlBundle.class.getName());
    }

    public SqlBundle(ConfigurationAccessor<T> accessor)
    {
        this.accessor = new CheckedConfigurationAccessor<>(accessor);
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        SqlConfiguration sqlConfiguration = accessor.accessConfiguration(configuration, SqlConfiguration.class);
        try
        {
            try ( InputStream stream = Resources.getResource(sqlConfiguration.getMybatisConfigUrl()).openStream() )
            {
                SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
                Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
                mybatisConfiguration.addMapper(AttributeEntityMapper.class);
                final SqlSession session = sqlSessionFactory.openSession();

                SoaConfiguration soaConfiguration = accessor.accessConfiguration(configuration, SoaConfiguration.class);
                soaConfiguration.putNamed(session, SqlBundle.class.getName());
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
        catch ( IOException e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }
}
