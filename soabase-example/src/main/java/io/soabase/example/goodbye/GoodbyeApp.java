package io.soabase.example.goodbye;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaFeatures;
import io.soabase.example.ExampleAppBase;
import io.soabase.example.ExampleConfiguration;

public class GoodbyeApp extends ExampleAppBase
{
    public static void main(String[] args) throws Exception
    {
        new GoodbyeApp().run(args);
    }

    public GoodbyeApp()
    {
        super("goodbye/config.json");
    }

    @Override
    protected void internalRun(ExampleConfiguration configuration, Environment environment)
    {
        environment.jersey().register(GoodbyeResource.class);
        JerseyEnvironment adminJerseyEnvironment = configuration.getSoaConfiguration().getNamedRequired(JerseyEnvironment.class, SoaFeatures.ADMIN_NAME);
        adminJerseyEnvironment.register(GoodbyeAdminResource.class);
    }
}
