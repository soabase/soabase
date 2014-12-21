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
package io.soabase.example.goodbye;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaFeatures;
import io.soabase.core.config.ComposedConfiguration;
import io.soabase.example.ExampleAppBase;

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
    protected void internalRun(ComposedConfiguration configuration, Environment environment)
    {
        SoaConfiguration soaConfiguration = configuration.access(SoaBundle.CONFIGURATION_NAME, SoaConfiguration.class);
        environment.jersey().register(GoodbyeResource.class);
        JerseyEnvironment adminJerseyEnvironment = soaConfiguration.getNamedRequired(JerseyEnvironment.class, SoaFeatures.ADMIN_NAME);
        adminJerseyEnvironment.register(GoodbyeAdminResource.class);
    }
}
