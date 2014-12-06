package io.soabase.core;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TestApplication extends SoaApplication<SoaConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        args = new String[]
            {
                "-c",
                "{" +
                    "\"attributes\":{" +
                    "\"type\": \"fuck\"," +
                    "\"mybatisConfigUrl\": \"foo\"," +
                    "}}",
                "-o",
                "attributes.type=sql",
                "-o",
                "attributes.mybatisConfigUrl=test-mybatis.xml"
            };
        SoaMain.run(TestApplication.class, args);
    }

    @Override
    protected void soaClose()
    {

    }

    @Override
    protected void soaRun(SoaConfiguration configuration, Environment environment)
    {
/*
        SoaFeatures features = environment.getApplicationContext().getBean(SoaFeatures.class);
        SqlDynamicAttributes attributes = (SqlDynamicAttributes)features.getAttributes();
        AttributeEntityMapper mapper = attributes.getSession().getMapper(AttributeEntityMapper.class);
        mapper.createDatabase();
        AttributeEntity attribute = new AttributeEntity("hey", "", "my value");
        mapper.insert(attribute);
        attribute.setfVALUE("yo yo yo");
        mapper.update(attribute);
*/
    }

    @Override
    protected void soaInitialize(Bootstrap<SoaConfiguration> bootstrap)
    {

    }
}
