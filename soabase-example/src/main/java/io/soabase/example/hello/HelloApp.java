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
        environment.jersey().register(HelloResourceJersey.class);
        environment.jersey().register(HelloResourceApache.class);
    }
}
