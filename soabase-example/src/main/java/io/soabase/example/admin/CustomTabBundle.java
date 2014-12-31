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

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.admin.components.ComponentManager;
import io.soabase.admin.components.TabComponent;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;

public class CustomTabBundle implements ConfiguredBundle<AdminConfiguration>
{
    @Override
    public void run(AdminConfiguration configuration, Environment environment) throws Exception
    {
        ComponentManager componentManager = SoaBundle.getFeatures(environment).getNamed(ComponentManager.class, SoaFeatures.DEFAULT_NAME);
        componentManager.addTab(new TabComponent("custom", "Custom Tab", "admin/custom/assets/custom.html"));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {

    }
}
