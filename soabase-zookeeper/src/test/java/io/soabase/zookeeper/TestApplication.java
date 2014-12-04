package io.soabase.zookeeper;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaApplication;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaMain;

public class TestApplication extends SoaApplication<SoaConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        args = new String[]
        {
            "-c",
            "{" +
                "\"discovery\":{" +
                "\"type\": \"zookeeper\"" +
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
