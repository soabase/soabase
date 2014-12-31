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

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.servlets.assets.AssetServlet;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.admin.components.AssetsPath;
import io.soabase.admin.components.ComponentBundle;
import io.soabase.admin.components.ComponentManager;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.rest.AttributesResource;
import io.soabase.admin.rest.DiscoveryResource;
import io.soabase.admin.rest.PreferencesResource;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;
import io.soabase.core.config.FlexibleConfigurationSourceProvider;

public abstract class SoaAdminApp<T extends SoaAdminConfiguration> extends Application<T>
{
    public SoaAdminApp()
    {
        System.setProperty("dw.soa.serviceName", "soabaseadmin");
        System.setProperty("dw.soa.addCorsFilter", "true");
        System.setProperty("dw.soa.registerInDiscovery", "false");
        System.setProperty("dw.server.rootPath", "/api/*");
    }

    @Override
    public final void initialize(Bootstrap<T> bootstrap)
    {
        bootstrap.setConfigurationSourceProvider(new FlexibleConfigurationSourceProvider());

        ConfiguredBundle<SoaAdminConfiguration> bundle = new ConfiguredBundle<SoaAdminConfiguration>()
        {
            @Override
            public void run(SoaAdminConfiguration configuration, Environment environment) throws Exception
            {
                DefaultServerFactory factory = new DefaultServerFactory();
                factory.setAdminConnectors(Lists.<ConnectorFactory>newArrayList());
                configuration.setServerFactory(factory);
            }

            @Override
            public void initialize(Bootstrap<?> bootstrap)
            {
                // NOP
            }
        };
        internalInitializePre(bootstrap);
        bootstrap.addBundle(bundle);
        bootstrap.addBundle(new SoaBundle<>());
        bootstrap.addBundle(new ComponentBundle<T>());
        bootstrap.addBundle(new AssetsBundle("/assets/soa"));
        internalInitializePost(bootstrap);
    }

    @Override
    public final void run(T configuration, Environment environment) throws Exception
    {
        environment.jersey().register(DiscoveryResource.class);
        environment.jersey().register(AttributesResource.class);
        environment.jersey().register(PreferencesResource.class);

        ComponentManager componentManager = SoaBundle.getFeatures(environment).getNamed(ComponentManager.class, SoaFeatures.DEFAULT_NAME);
        for ( TabComponent component : componentManager.getTabs() )
        {
            int index = 0;
            for ( AssetsPath assetsPath : component.getAssetsPaths() )
            {
                AssetServlet servlet = new AssetServlet(assetsPath.getResourcePath(), assetsPath.getUriPath(), null, Charsets.UTF_8);
                environment.servlets().addServlet(component.getName() + index++, servlet).addMapping(assetsPath.getUriPath() + '*');
            }
        }

        internalRun(configuration, environment);
    }

    protected abstract void internalInitializePre(Bootstrap<T> bootstrap);

    protected abstract void internalInitializePost(Bootstrap<T> bootstrap);

    protected abstract void internalRun(T configuration, Environment environment);
}
