package io.soabase.sql.attributes;

import com.google.common.io.Resources;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.InputStream;

public class SoaSqlBundle<T extends SoaConfiguration & SoaSqlConfigurationAccessor> implements ConfiguredBundle<T>
{
    public static SqlSession getSqlSession(SoaConfiguration configuration)
    {
        return configuration.getNamed(SqlSession.class, SoaSqlBundle.class.getName());
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        SoaSqlConfiguration sqlConfiguration = configuration.getSqlConfiguration();
        try ( InputStream stream = Resources.getResource(sqlConfiguration.getMybatisConfigUrl()).openStream() )
        {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
            Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
            mybatisConfiguration.addMapper(AttributeEntityMapper.class);
            final SqlSession session = sqlSessionFactory.openSession();

            configuration.putNamed(session, SoaSqlBundle.class.getName());
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

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }
}
