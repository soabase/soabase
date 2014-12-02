package io.soabase.core;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.SoaBaseFeatures;
import io.soabase.sql.attributes.AttributeEntity;
import io.soabase.sql.attributes.AttributeEntityMapper;
import io.soabase.sql.attributes.SqlDynamicAttributes;

public class TestApplication extends SoaBaseApplication<SoaBaseConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        args = new String[]
            {
                "-c",
                "{" +
                    "\"attributes\":{" +
                    "\"type\": \"sql\"," +
                    "\"mybatisConfigUrl\": \"test-mybatis.xml\"," +
                    "}}"
            };
        SoaBaseMain.run(TestApplication.class, args);
    }

    @Override
    protected void soaClose()
    {

    }

    @Override
    protected void soaRun(SoaBaseFeatures features, SoaBaseConfiguration configuration, Environment environment)
    {
        SqlDynamicAttributes attributes = (SqlDynamicAttributes)features.getAttributes();
        AttributeEntityMapper mapper = attributes.getSession().getMapper(AttributeEntityMapper.class);
        mapper.createDatabase();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
    }

    @Override
    protected void soaInitialize(Bootstrap<SoaBaseConfiguration> bootstrap)
    {

    }
}
