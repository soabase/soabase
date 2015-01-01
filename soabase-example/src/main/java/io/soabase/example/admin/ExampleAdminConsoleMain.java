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

import io.soabase.admin.AdminConsoleApp;
import io.soabase.admin.AdminConsoleAppBuilder;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.components.TabComponentBuilder;
import io.soabase.example.ExampleAppBase;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.zookeeper.discovery.CuratorBundle;

public class ExampleAdminConsoleMain
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        TabComponent component = TabComponentBuilder.builder()
            .withId("custom")
            .withName("Custom Tab")
            .withContentResourcePath("admin/custom/assets/custom.html")
            .addingAssetsPath("/admin/custom/assets")
            .addingJavascriptUriPath("/admin/custom/assets/js/custom.js")
            .addingCssUriPath("/admin/custom/assets/css/custom.css")
            .build();

        AdminConsoleApp<ExampleAdminConfiguration> app = AdminConsoleAppBuilder.<ExampleAdminConfiguration>builder()
            .withAppName("Example")
            .withCompanyName("My Company")
            .withConfigurationClass(ExampleAdminConfiguration.class)
            .addingTabComponent(component)
            .addingPreSoaBundle(new CuratorBundle<ExampleAdminConfiguration>())
            .addingPreSoaBundle(new SqlBundle<ExampleAdminConfiguration>())
            .build()
            ;
        app.run(ExampleAppBase.setSystemAndAdjustArgs("admin/config.json"));
    }
}
