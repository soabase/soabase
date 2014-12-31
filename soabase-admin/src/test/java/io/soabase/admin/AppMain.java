package io.soabase.admin;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.zookeeper.discovery.CuratorBundle;

public class AppMain extends SoaAdminApp<AdminConfiguration>
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        args = new String[]
        {
            "server",
            "|config.json"
        };
        new AppMain().run(args);
    }

    @Override
    protected void internalInitialize(Bootstrap<AdminConfiguration> bootstrap)
    {
        bootstrap.addBundle(new CuratorBundle<>());
        bootstrap.addBundle(new SqlBundle<>());
    }

    @Override
    protected void internalRun(AdminConfiguration configuration, Environment environment)
    {
        // NOP
    }
}
