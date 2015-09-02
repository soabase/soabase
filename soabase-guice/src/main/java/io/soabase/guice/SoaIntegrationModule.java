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
package io.soabase.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.ExecutorBuilder;
import io.soabase.core.features.attributes.DynamicAttributes;
import io.soabase.core.features.discovery.Discovery;
import io.soabase.core.features.discovery.deployment.DeploymentGroupManager;

public class SoaIntegrationModule extends AbstractModule
{
    private final Environment environment;

    public SoaIntegrationModule(Environment environment)
    {
        this.environment = environment;
    }

    @Override
    protected void configure()
    {
        SoaFeatures features = SoaBundle.getFeatures(environment);
        bind(SoaFeatures.class).toInstance(features);
        if ( features.getDiscovery() != null )
        {
            bind(Discovery.class).toInstance(features.getDiscovery());
        }
        if ( features.getAttributes() != null )
        {
            bind(DynamicAttributes.class).toInstance(features.getAttributes());
        }
        if ( features.getSoaInfo() != null )
        {
            bind(SoaInfo.class).toInstance(features.getSoaInfo());
        }
        if ( features.getExecutorBuilder() != null )
        {
            bind(ExecutorBuilder.class).toInstance(features.getExecutorBuilder());
        }
        if ( features.getDeploymentGroupManager() != null )
        {
            bind(DeploymentGroupManager.class).toInstance(features.getDeploymentGroupManager());
        }

        for ( Class<?> clazz : features.getClasses() )
        {
            for ( String name : features.getNames(clazz) )
            {
                Object named = features.getNamed(clazz, name);
                //noinspection unchecked
                bind((Class)clazz).annotatedWith(Names.named(name)).toInstance(named);
                if ( name.equals(SoaFeatures.DEFAULT_NAME) )
                {
                    //noinspection unchecked
                    bind((Class)clazz).toInstance(named);
                }
            }
        }
    }
}
