package io.soabase.core;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.sql.attributes.AttributeEntity;
import io.soabase.sql.attributes.AttributeEntityMapper;
import io.soabase.sql.attributes.SoaSqlBundle;
import org.apache.ibatis.session.SqlSession;

public class TestApplication extends SoaApplication<TestConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        args = new String[]
            {
                "-c",
                "{" +
                    "\"attributes\":{" +
                    "\"type\": \"fuck\"," +
                    "}}",
                "-o",
                "attributes.type=sql",
                "-o",
                "sql.mybatisConfigUrl=test-mybatis.xml"
            };
        SoaMain.run(TestApplication.class, args);
    }

    @Override
    protected void soaClose()
    {

    }

    @Override
    protected void soaRun(TestConfiguration configuration, Environment environment)
    {
        SqlSession sqlSession = SoaSqlBundle.getSqlSession(configuration);
        AttributeEntityMapper mapper = sqlSession.getMapper(AttributeEntityMapper.class);
        mapper.createDatabase();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
    }

    @Override
    protected void soaInitialize(Bootstrap<TestConfiguration> bootstrap)
    {
        bootstrap.addBundle(new SoaSqlBundle<TestConfiguration>());
    }
}
