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
package io.soabase.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.soabase.admin.auth.AuthSpec;
import io.soabase.admin.components.MetricComponent;
import io.soabase.admin.components.StandardComponents;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.details.BundleSpec;
import javax.validation.Validator;
import java.util.List;

public class AdminConsoleAppBuilder<T extends Configuration>
{
    private String appName = "Soabase";
    private String companyName = "";
    private String footerMessage = "- Internal use only - Proprietary and Confidential";
    private String soaConfigFieldName = "soa";
    private AuthSpec authSpec = null;
    private final List<TabComponent> tabs = Lists.newArrayList();
    private final List<BundleSpec<T>> bundles = Lists.newArrayList();
    private final List<MetricComponent> metrics = Lists.newArrayList();
    private ConfigurationFactoryFactory<T> factoryFactory = null;

    public static <T extends Configuration> AdminConsoleAppBuilder<T> builder()
    {
        return new AdminConsoleAppBuilder<>();
    }

    public AdminConsoleApp<T> build()
    {
        return new AdminConsoleApp<>(this);
    }

    public AdminConsoleAppBuilder<T> withSoaConfigFieldName(String soaConfigFieldName)
    {
        this.soaConfigFieldName = Preconditions.checkNotNull(soaConfigFieldName, "soaConfigFieldName cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withConfigurationClass(final Class<T> configurationClass)
    {
        factoryFactory = new ConfigurationFactoryFactory<T>()
        {
            @Override
            public ConfigurationFactory<T> create(Class<T> klass, Validator validator, ObjectMapper objectMapper, String propertyPrefix)
            {
                return new ConfigurationFactory<>(configurationClass, validator, objectMapper, propertyPrefix);
            }
        };
        return this;
    }

    public AdminConsoleAppBuilder<T> withConfigurationClassFactory(ConfigurationFactoryFactory<T> factoryFactory)
    {
        this.factoryFactory = Preconditions.checkNotNull(factoryFactory, "factoryFactory cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withAppName(String appName)
    {
        this.appName = Preconditions.checkNotNull(appName, "appName cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withCompanyName(String companyName)
    {
        this.companyName = Preconditions.checkNotNull(companyName, "companyName cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withFooterMessage(String footerMessage)
    {
        this.footerMessage = Preconditions.checkNotNull(footerMessage, "footerMessage cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withAuthSpec(AuthSpec authSpec)
    {
        this.authSpec = authSpec;
        return this;
    }

    public AdminConsoleAppBuilder<T> addingTabComponent(TabComponent tabComponent)
    {
        tabComponent = Preconditions.checkNotNull(tabComponent, "tabComponent cannot be null");
        tabs.add(tabComponent);
        return this;
    }

    public AdminConsoleAppBuilder<T> addingMetricComponent(MetricComponent metricComponent)
    {
        metricComponent = Preconditions.checkNotNull(metricComponent, "metricsComponent cannot be null");
        metrics.add(metricComponent);
        return this;
    }

    public AdminConsoleAppBuilder<T> addingBundle(BundleSpec<T> bundleSpec)
    {
        bundleSpec = Preconditions.checkNotNull(bundleSpec, "bundleSpec cannot be null");
        bundles.add(bundleSpec);
        return this;
    }

    public AdminConsoleAppBuilder<T> clearingComponents()
    {
        tabs.clear();
        metrics.clear();
        return this;
    }

    String getAppName()
    {
        return appName;
    }

    String getCompanyName()
    {
        return companyName;
    }

    String getFooterMessage()
    {
        return footerMessage;
    }

    String getSoaConfigFieldName()
    {
        return soaConfigFieldName;
    }

    List<TabComponent> getTabs()
    {
        return tabs;
    }

    List<BundleSpec<T>> getBundles()
    {
        return bundles;
    }

    ConfigurationFactoryFactory<T> getFactoryFactory()
    {
        return factoryFactory;
    }

    List<MetricComponent> getMetrics()
    {
        return metrics;
    }

    AuthSpec getAuthSpec()
    {
        return authSpec;
    }

    private AdminConsoleAppBuilder()
    {
        tabs.add(StandardComponents.newServicesTab());
        tabs.add(StandardComponents.newAttributesTab());
        metrics.add(StandardComponents.newHeapMetric());
        metrics.add(StandardComponents.newThreadsMetric());
        metrics.add(StandardComponents.newCpuMetric());
        metrics.add(StandardComponents.newRequestsMetric());
        metrics.add(StandardComponents.newGcMetric());
        metrics.add(StandardComponents.newGcTimesMetric());
        metrics.add(StandardComponents.newRequestStatusMetric());
    }
}
