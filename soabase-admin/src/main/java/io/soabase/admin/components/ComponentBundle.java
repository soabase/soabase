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

import com.google.common.collect.Lists;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.admin.IndexServlet;
import io.soabase.admin.SoaAdminConfiguration;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.util.prefs.Preferences;

public class ComponentBundle<T extends SoaAdminConfiguration> implements ConfiguredBundle<T>
{
    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        final ComponentManager componentManager = new ComponentManager(configuration.getAppName(), configuration.getCompany(), configuration.getFooterMessage());
        final Preferences preferences = Preferences.userRoot();
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(preferences).to(Preferences.class);
                bind(componentManager).to(ComponentManager.class);
            }
        };
        SoaBundle.getFeatures(environment).putNamed(componentManager, ComponentManager.class, SoaFeatures.DEFAULT_NAME);
        SoaBundle.getFeatures(environment).putNamed(preferences, Preferences.class, SoaFeatures.DEFAULT_NAME);

        componentManager.addTab(new TabComponent("", "Services", "assets/main.html", Lists.newArrayList("assets/js/main.js"), Lists.newArrayList("assets/css/main.css")));
        componentManager.addTab(new TabComponent("soa-attributes", "Attributes", "assets/attributes.html", Lists.newArrayList("assets/js/attributes.js"), Lists.newArrayList("assets/css/attributes.css")));

        environment.servlets().addServlet("index", new IndexServlet(componentManager)).addMapping("");

        environment.jersey().register(binder);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }
}
