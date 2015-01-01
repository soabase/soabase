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
package io.soabase.admin.components;

public class DefaultComponents
{
    public static MetricComponent newGcMetric()
    {
        return new MetricComponent("soa-gc", MetricType.DELTA, "GC", "/jvm\\.gc\\./", "/\\.count/", "# of GCs");
    }

    public static TabComponent newServicesTab()
    {
        return TabComponentBuilder.builder()
            .withId("soa-services")
            .withName("Services")
            .withContentResourcePath("assets/services/services.html")
            .addingJavascriptUriPath("/assets/services/js/services.js")
            .addingCssUriPath("/assets/services/css/services.css")
            .addingAssetsPath("/assets/services/js")
            .addingAssetsPath("/assets/services/css")
            .build();
    }

    public static TabComponent newAttributesTab()
    {
        return TabComponentBuilder.builder()
            .withId("soa-attributes")
            .withName("Attributes")
            .withContentResourcePath("assets/attributes/attributes.html")
            .addingJavascriptUriPath("/assets/attributes/js/attributes.js")
            .addingCssUriPath("/assets/attributes/css/attributes.css")
            .addingAssetsPath("/assets/attributes/js")
            .addingAssetsPath("/assets/attributes/css")
            .build();
    }

    private DefaultComponents()
    {
    }
}
