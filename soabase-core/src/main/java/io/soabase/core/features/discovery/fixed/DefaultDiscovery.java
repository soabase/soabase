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
package io.soabase.core.features.discovery.fixed;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.discovery.Discovery;
import io.soabase.core.features.discovery.DiscoveryInstance;
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.HealthyState;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

class DefaultDiscovery implements Discovery
{
    private final SoaInfo soaInfo;
    private final Multimap<String, DiscoveryInstance> instances;
    private volatile Map<String, String> metaData;
    private volatile HealthyState healthyState;

    public DefaultDiscovery(SoaInfo soaInfo, List<Instance> instances)
    {
        this.soaInfo = soaInfo;
        metaData = ImmutableMap.of();
        healthyState = HealthyState.HEALTHY;

        ImmutableListMultimap.Builder<String, DiscoveryInstance> builder = ImmutableListMultimap.builder();
        for ( Instance instance : instances )
        {
            HostAndPort mainPort = HostAndPort.fromParts(instance.getHost(), instance.getPort());
            HostAndPort adminPort = HostAndPort.fromParts(instance.getHost(), instance.getAdminPort());
            SoaInfo thisInfo = new SoaInfo(Lists.<String>newArrayList(), mainPort, adminPort, instance.getServiceName(), "", true);
            builder.put(instance.getServiceName(), newDiscoveryInstance(thisInfo, HealthyState.HEALTHY, Maps.<String, String>newHashMap()));
        }
        this.instances = builder.build();
    }

    public Collection<String> getServiceNames()
    {
        Set<String> names = Sets.newHashSet(soaInfo.getServiceName());
        names.addAll(instances.keySet());
        return names;
    }

    @Override
    public Collection<DiscoveryInstance> getAllInstances(String serviceName)
    {
        if ( serviceName.equals(soaInfo.getServiceName()) )
        {
            return Collections.singletonList(getInstance(serviceName));
        }
        if ( instances.containsKey(serviceName) )
        {
            return instances.get(serviceName);
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
        Collection<DiscoveryInstance> discoveryInstances = instances.get(serviceName);
        if ( (discoveryInstances != null) && (discoveryInstances.size() > 0) )
        {
            List<DiscoveryInstance> localList = Lists.newArrayList(discoveryInstances);
            Collections.shuffle(localList);
            return localList.get(0);
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
