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

import com.google.common.collect.Lists;
import io.soabase.admin.AdminConsoleApp;
import io.soabase.admin.AdminConsoleAppBuilder;
import io.soabase.admin.components.Metric;
import io.soabase.admin.components.MetricComponent;
import io.soabase.admin.components.MetricType;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.components.TabComponentBuilder;
import io.soabase.admin.details.BundleSpec;
import io.soabase.example.ExampleAppBase;
import io.soabase.sql.attributes.SqlBundle;
import io.soabase.zookeeper.discovery.CuratorBundle;
import java.util.List;

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

        List<Metric> metrics = Lists.newArrayList(new Metric("random", "gauges['goodbye-random'].value"));
        MetricComponent customMetric = new MetricComponent("custom-metric", MetricType.STANDARD, "Custom", "Value", metrics, "goodbye");    // custom metric only for "Goodbye" app
        AdminConsoleApp<ExampleAdminConfiguration> app = AdminConsoleAppBuilder.<ExampleAdminConfiguration>builder()
            .withAppName("Example")
            .withCompanyName("My Company")
            .withConfigurationClass(ExampleAdminConfiguration.class)
            .addingTabComponent(component)
            .addingMetricComponent(customMetric)
            .addingBundle(new BundleSpec<>(new CuratorBundle<ExampleAdminConfiguration>(), BundleSpec.Phase.PRE_SOA))
            .addingBundle(new BundleSpec<>(new SqlBundle<ExampleAdminConfiguration>(), BundleSpec.Phase.PRE_SOA))
            .build()
            ;
        app.run(ExampleAppBase.setSystemAndAdjustArgs("admin/config.json"));
    }
}
