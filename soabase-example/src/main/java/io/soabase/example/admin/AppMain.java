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
package io.soabase.example.admin;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.admin.SoaAdminApp;
import io.soabase.example.ExampleAppBase;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.zookeeper.discovery.CuratorBundle;

public class AppMain extends SoaAdminApp<AdminConfiguration>
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        new AppMain().run(ExampleAppBase.setSystemAndAdjustArgs("admin/config.json"));
    }

    @Override
    protected void internalInitializePre(Bootstrap<AdminConfiguration> bootstrap)
    {
        bootstrap.addBundle(new CuratorBundle<>());
        bootstrap.addBundle(new SqlBundle<>());
    }

    @Override
    protected void internalInitializePost(Bootstrap<AdminConfiguration> bootstrap)
    {
        bootstrap.addBundle(new CustomTabBundle());
    }

    @Override
    protected void internalRun(AdminConfiguration configuration, Environment environment)
    {
        // NOP
    }
}
