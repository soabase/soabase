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
package io.soabase.guice.example;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.guice.GuiceBundle;
import io.soabase.guice.InjectorProvider;
import io.soabase.guice.StandardInjectorProvider;

public class ExampleApplication extends Application<Configuration>
{
    public static void main(String[] args) throws Exception
    {
        new ExampleApplication().run("server");
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap)
    {
        // Use the StandardInjectorProvider unless you need special behavior - you can pass as many modules as you like
        InjectorProvider<Configuration> injectorProvider = new StandardInjectorProvider<>(new ExampleJerseyGuiceModule());
        bootstrap.addBundle(new GuiceBundle<>(injectorProvider));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception
    {
        // NOP
    }
}
