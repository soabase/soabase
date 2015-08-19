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
package io.soabase.zookeeper.discovery;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.soabase.core.features.discovery.DiscoveryInstance;
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.HealthyState;
import org.codehaus.jackson.annotate.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Payload
{
    private String adminHost;
    private int adminPort;
    private Map<String, String> metaData;
    private ForcedState forcedState;
    private HealthyState healthyState;

    public Payload()
    {
        this(null, 0, Maps.<String, String>newHashMap(), ForcedState.CLEARED, HealthyState.HEALTHY);
    }

    public Payload(String adminHost, int adminPort, Map<String, String> metaData, ForcedState forcedState, HealthyState healthyState)
    {
        this.adminHost = Preconditions.checkNotNull(adminHost, "adminHost cannot be null");
        this.forcedState = Preconditions.checkNotNull(forcedState, "forcedState cannot be null");
        this.healthyState = Preconditions.checkNotNull(healthyState, "healthyState cannot be null");
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.adminPort = adminPort;
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    public String getAdminHost()
    {
        return adminHost;
    }

    public void setAdminHost(String adminHost)
    {
        this.adminHost = adminHost;
    }

    public int getAdminPort()
    {
        return adminPort;
    }

    public void setAdminPort(int adminPort)
    {
        this.adminPort = adminPort;
    }

    public static void addDeploymentGroups(Map<String, String> metaData, Collection<String> deploymentGroups)
    {
        if ( deploymentGroups.size() > 0 )
        {
            metaData.put(DiscoveryInstance.META_DATA_KEY_DEPLOYMENT_GROUP, Joiner.on(',').join(deploymentGroups));
        }
    }

    @JsonIgnore
    public Collection<String> getDeploymentGroups()
    {
        if ( metaData != null )
        {
            String value = metaData.get(DiscoveryInstance.META_DATA_KEY_DEPLOYMENT_GROUP);
            if ( value != null )
            {
                return Splitter.on(',').trimResults().omitEmptyStrings().splitToList(value);
            }
        }
        return Collections.emptySet();
    }

    public Map<String, String> getMetaData()
    {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData)
    {
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    public ForcedState getForcedState()
    {
        return forcedState;
    }

    public HealthyState getHealthyState()
    {
        return healthyState;
    }

    public void setForcedState(ForcedState forcedState)
    {
        this.forcedState = forcedState;
    }

    public void setHealthyState(HealthyState healthyState)
    {
        this.healthyState = healthyState;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Payload payload = (Payload)o;

        if ( adminPort != payload.adminPort )
        {
            return false;
        }
        if ( !adminHost.equals(payload.adminHost) )
        {
            return false;
        }
        if ( !metaData.equals(payload.metaData) )
        {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if ( forcedState != payload.forcedState )
        {
            return false;
        }
        return healthyState == payload.healthyState;

    }

    @Override
    public int hashCode()
    {
        int result = adminHost.hashCode();
        result = 31 * result + adminPort;
        result = 31 * result + metaData.hashCode();
        result = 31 * result + forcedState.hashCode();
        result = 31 * result + healthyState.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Payload{" +
            "adminHost='" + adminHost + '\'' +
            ", adminPort=" + adminPort +
            ", metaData=" + metaData +
            ", forcedState=" + forcedState +
            ", healthyState=" + healthyState +
            '}';
    }
}
