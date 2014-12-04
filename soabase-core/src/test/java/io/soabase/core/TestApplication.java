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
                "\"server\":{" +
                "\"type\": \"default\"" +
            "}}"
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

    }

    @Override
    protected void soaInitialize(Bootstrap<SoaConfiguration> bootstrap)
    {

    }
}
