package io.soabase.zookeeper;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaBaseApplication;
import io.soabase.core.SoaBaseConfiguration;
import io.soabase.core.SoaBaseMain;
import io.soabase.core.features.SoaBaseFeatures;

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
    protected void soaRun(SoaBaseFeatures features, SoaBaseConfiguration configuration, Environment environment)
    {

    }

    @Override
    protected void soaInitialize(Bootstrap<SoaBaseConfiguration> bootstrap)
    {

    }
}
