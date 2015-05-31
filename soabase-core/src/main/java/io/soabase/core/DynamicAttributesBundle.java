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
package io.soabase.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.attributes.DynamicAttributes;
import io.soabase.core.features.attributes.StandardAttributesContainer;
import io.soabase.core.features.config.ComposedConfigurationAccessor;
import io.soabase.core.features.config.SoaConfiguration;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

class DynamicAttributesBundle implements ConfiguredBundle<Configuration>
{
    static class Scopes
    {
        private final List<String> scopes;

        Scopes(List<String> scopes)
        {
            this.scopes = ImmutableList.copyOf(scopes);
        }

        List<String> getScopes()
        {
            return scopes;
        }
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception
    {
        SoaConfiguration soaConfiguration = ComposedConfigurationAccessor.access(configuration, environment, SoaConfiguration.class);

        updateInstanceName(soaConfiguration);
        List<String> scopes = Lists.newArrayList();
        scopes.add(soaConfiguration.getInstanceName());
        scopes.add(soaConfiguration.getServiceName());
        scopes.addAll(soaConfiguration.getScopes());
        environment.getApplicationContext().setAttribute(DynamicAttributesBundle.Scopes.class.getName(), new Scopes(scopes));

        // attributes must be allocated first - Discovery et al might need them
        DynamicAttributes attributes = StandardAttributesContainer.wrapAttributes(SoaBundle.checkManaged(environment, soaConfiguration.getAttributesFactory().build(configuration, environment, scopes)), SoaBundle.hasAdminKey);
        environment.getApplicationContext().setAttribute(DynamicAttributes.class.getName(), attributes);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    private void updateInstanceName(SoaConfiguration configuration) throws UnknownHostException
    {
        if ( configuration.getInstanceName() == null )
        {
            configuration.setInstanceName(InetAddress.getLocalHost().getHostName());
        }
    }
}
