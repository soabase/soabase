package io.soabase.core;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TestApplication extends SoaBaseApplication<SoaBaseConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        args = new String[]
            {
                "-c",
                "{" +
                    "\"attributes\":{" +
                    "\"type\": \"sql\"" +
                    "}}"
            };
        SoaBaseMain.run(TestApplication.class, args);
    }

    @Override
    protected void soaClose()
    {

    }

    @Override
    protected void soaRun(SoaBaseConfiguration configuration, Environment environment)
    {

    }

    @Override
    protected void soaInitialize(Bootstrap<SoaBaseConfiguration> bootstrap)
    {

    }
}
