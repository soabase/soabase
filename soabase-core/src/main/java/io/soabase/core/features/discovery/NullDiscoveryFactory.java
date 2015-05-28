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
package io.soabase.core.features.discovery;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaInfo;
import java.util.Collection;
import java.util.Map;

@JsonTypeName("default")
public class NullDiscoveryFactory implements DiscoveryFactory
{
    @Override
    public Discovery build(Configuration configuration, Environment environment, SoaInfo soaInfo)
    {
        return new Discovery()
        {
            @Override
            public Collection<String> getServiceNames()
            {
                return ImmutableSet.of();
            }

            @Override
            public Collection<DiscoveryInstance> getAllInstances(String serviceName)
            {
                return ImmutableSet.of();
            }

            @Override
            public DiscoveryInstance getInstance(String serviceName)
            {
                return null;
            }

            @Override
            public void noteError(String serviceName, DiscoveryInstance errorInstance, int statusCode, Throwable exception)
            {
                // NOP
            }

            @Override
            public void noteSuccess(String serviceName, DiscoveryInstance instance, int statusCode)
            {
                // NOP
            }

            @Override
            public void setMetaData(Map<String, String> newMetaData)
            {
                // NOP
            }

            @Override
            public void setHealthyState(HealthyState healthyState)
            {
                // NOP
            }
        };
    }
}
