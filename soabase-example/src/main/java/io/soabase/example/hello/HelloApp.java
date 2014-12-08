package io.soabase.example.hello;

import io.dropwizard.setup.Environment;
import io.soabase.example.ExampleAppBase;
import io.soabase.example.ExampleConfiguration;

public class HelloApp extends ExampleAppBase
{
    public static void main(String[] args) throws Exception
    {
        new HelloApp().run(args);
    }

    public HelloApp()
    {
        super("hello/config.json");
    }

    @Override
    protected void internalRun(ExampleConfiguration configuration, Environment environment)
    {
        environment.jersey().register(HelloResource.class);
    }
}
