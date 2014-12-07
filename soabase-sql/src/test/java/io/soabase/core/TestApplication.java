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
        args = new String[]
            {
                "-o",
                "soa.attributes.type=sql",
                "-o",
                "sql.mybatisConfigUrl=test-mybatis.xml"
            };
        args = SoaCli.parseArgs(args);
        new TestApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap)
    {
        ConfigurationAccessor<TestConfiguration> accessor = new ConfigurationAccessor<TestConfiguration>()
        {
            @Override
            public <T> T accessConfiguration(TestConfiguration configuration, Class<T> clazz)
            {
                if ( clazz.equals(SqlConfiguration.class) )
                {
                    return clazz.cast(configuration.getSqlConfiguration());
                }
                if ( clazz.equals(SoaConfiguration.class) )
                {
                    return clazz.cast(configuration.getSoaConfiguration());
                }
                return null;
            }
        };
        bootstrap.addBundle(new SqlBundle<>(accessor));
        bootstrap.addBundle(new SoaBundle<>(accessor));
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception
    {
        SqlSession sqlSession = SqlBundle.getSqlSession(configuration.getSoaConfiguration());
        AttributeEntityMapper mapper = sqlSession.getMapper(AttributeEntityMapper.class);
        mapper.createDatabase();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
    }
}
