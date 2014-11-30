package io.soabase.zookeeper;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaBaseApplication;
import io.soabase.core.SoaBaseConfiguration;
import io.soabase.core.SoaBaseMain;

public class TestApplication extends SoaBaseApplication<SoaBaseConfiguration>
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
