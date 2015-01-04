/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.example.hello;

import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import io.soabase.client.ClientBuilder;
import io.soabase.core.SoaFeatures;
import io.soabase.example.ExampleAppBase;

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
    protected void internalRun(Configuration configuration, Environment environment)
    {
        ClientBuilder builder = new ClientBuilder(environment);
        builder.buildJerseyClient(new JerseyClientConfiguration(), SoaFeatures.DEFAULT_NAME);
        builder.buildHttpClient(new HttpClientConfiguration(), SoaFeatures.DEFAULT_NAME);

        environment.jersey().register(HelloResourceJersey.class);
        environment.jersey().register(HelloResourceApache.class);
    }
}
