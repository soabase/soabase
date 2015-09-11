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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@JsonTypeName("default")
public class NullDiscoveryFactory implements DiscoveryFactory
{
    @Override
    public Discovery build(Configuration configuration, Environment environment, final SoaInfo soaInfo)
    {
        return new Discovery()
        {
            private volatile Map<String, String> metaData = ImmutableMap.of();
            private volatile HealthyState healthyState = HealthyState.HEALTHY;

            public Collection<String> getServiceNames()
            {
                return Collections.singletonList(soaInfo.getServiceName());
            }

            @Override
            public Collection<DiscoveryInstance> getAllInstances(String serviceName)
            {
                if ( serviceName.equals(soaInfo.getServiceName()) )
                {
                    return Collections.singletonList(getInstance(serviceName));
                }
                return ImmutableSet.of();
            }

            @Override
            public DiscoveryInstance getInstance(String serviceName)
            {
                if ( serviceName.equals(soaInfo.getServiceName()) )
                {
                    return newDiscoveryInstance(soaInfo, healthyState, metaData);
                }
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
                metaData = ImmutableMap.copyOf(newMetaData);
            }

            @Override
            public void setHealthyState(HealthyState healthyState)
            {
                this.healthyState = healthyState;
            }
        };
    }

    private DiscoveryInstance newDiscoveryInstance(final SoaInfo soaInfo, final HealthyState healthyState, final Map<String, String> newMetaData)
    {
        final String id = UUID.randomUUID().toString();
        return new DiscoveryInstance()
        {
            @Override
            public String getId()
            {
                return id;
            }

            @Override
            public String getHost()
            {
                return soaInfo.getMainPort().getHostText();
            }

            @Override
            public int getPort()
            {
                return soaInfo.getMainPort().getPort();
            }

            @Override
            public boolean isForceSsl()
            {
                return false;
            }

            @Override
            public String getAdminHost()
            {
                return soaInfo.getAdminPort().getHostText();
            }

            @Override
            public int getAdminPort()
            {
                return soaInfo.getAdminPort().getPort();
            }

            @Override
            public HealthyState getHealthyState()
            {
                return healthyState;
            }

            @Override
            public Map<String, String> getMetaData()
            {
                return newMetaData;
            }

            @Override
            public ForcedState getForcedState()
            {
                return ForcedState.CLEARED;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public int compareTo(DiscoveryInstance o)
            {
                if ( o == null )
                {
                    return -1;
                }
                return id.compareTo(o.getId());
            }
        };
    }
}
