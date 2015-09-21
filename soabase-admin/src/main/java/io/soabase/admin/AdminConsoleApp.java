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
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.servlets.assets.AssetServlet;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.admin.auth.AuthSpecHolder;
import io.soabase.admin.components.AssetsPath;
import io.soabase.admin.components.ComponentBundle;
import io.soabase.admin.components.ComponentManager;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.details.BundleSpec;
import io.soabase.admin.rest.AttributesResource;
import io.soabase.admin.rest.AuthResource;
import io.soabase.admin.rest.DiscoveryResource;
import io.soabase.admin.rest.PreferencesResource;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.config.FlexibleConfigurationSourceProvider;
import org.eclipse.jetty.server.session.SessionHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AdminConsoleApp<T extends Configuration> extends Application<T>
{
    private final AdminConsoleAppBuilder<T> builder;

    AdminConsoleApp(AdminConsoleAppBuilder<T> builder)
    {
        System.setProperty(makeSoaConfig(builder, "serviceName"), "soabaseadmin");
        System.setProperty(makeSoaConfig(builder, "addCorsFilter"), "true");
        System.setProperty(makeSoaConfig(builder, "registerInDiscovery"), "false");
        System.setProperty("dw.server.rootPath", "/api/*");

        this.builder = builder;
    }

    @Override
    public final void initialize(Bootstrap<T> bootstrap)
    {
        bootstrap.setConfigurationFactoryFactory(builder.getFactoryFactory());
        bootstrap.setConfigurationSourceProvider(new FlexibleConfigurationSourceProvider());

        ConfiguredBundle<T> bundle = new ConfiguredBundle<T>()
        {
            @Override
            public void run(T configuration, Environment environment) throws Exception
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

        addBundles(bootstrap, BundleSpec.Phase.PRE_SOA);
        bootstrap.addBundle(bundle);
        bootstrap.addBundle(new SoaBundle<>());
        bootstrap.addBundle(new ComponentBundle(builder.getAppName(), builder.getCompanyName(), builder.getFooterMessage(), builder.getTabs(), builder.getMetrics(), builder.getAuthSpec()));
        bootstrap.addBundle(new AssetsBundle("/assets/soa"));
        addBundles(bootstrap, BundleSpec.Phase.POST_SOA);
    }

    @Override
    public final void run(T configuration, Environment environment) throws Exception
    {
        environment.jersey().register(DiscoveryResource.class);
        environment.jersey().register(AttributesResource.class);
        environment.jersey().register(PreferencesResource.class);

        environment.jersey().register(AuthResource.class);
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                AuthSpecHolder holder = new AuthSpecHolder(builder.getAuthSpec());
                bind(holder).to(AuthSpecHolder.class);
            }
        };
        environment.jersey().register(binder);
        if ( builder.getAuthSpec() != null )
        {
            environment.servlets().setSessionHandler(new SessionHandler());
        }

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

    }

    private void addBundles(Bootstrap<T> bootstrap, BundleSpec.Phase phase)
    {
        for ( BundleSpec<T> bundleSpec : builder.getBundles() )
        {
            if ( bundleSpec.getPhase() == phase )
            {
                if ( bundleSpec.getBundle() != null )
                {
                    bootstrap.addBundle(bundleSpec.getBundle());
                }
                else
                {
                    bootstrap.addBundle(bundleSpec.getConfiguredBundle());
                }
            }
        }
    }

    private static String makeSoaConfig(AdminConsoleAppBuilder<?> builder, String name)
    {
        StringBuilder str = new StringBuilder("dw.");
        if ( builder.getSoaConfigFieldName().length() > 0 )
        {
            str.append(builder.getSoaConfigFieldName()).append(".");
        }
        str.append(name);
        return str.toString();
    }
}
