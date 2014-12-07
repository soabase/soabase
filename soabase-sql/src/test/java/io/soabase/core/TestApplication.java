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
        SqlSession sqlSession = configuration.getSoaConfiguration().getNamed(SqlSession.class, SoaFeatures.DEFAULT_NAME);
        AttributeEntityMapper mapper = sqlSession.getMapper(AttributeEntityMapper.class);
        mapper.createTable();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
    }
}
