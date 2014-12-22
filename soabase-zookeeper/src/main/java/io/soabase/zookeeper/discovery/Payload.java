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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.HealthyState;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class Payload
{
    private int adminPort;
    private Map<String, String> metaData;
    private ForcedState forcedState;
    private HealthyState healthyState;

    public Payload()
    {
        this(0, Maps.<String, String>newHashMap(), ForcedState.CLEARED, HealthyState.HEALTHY);
    }

    public Payload(int adminPort, Map<String, String> metaData, ForcedState forcedState, HealthyState healthyState)
    {
        this.forcedState = Preconditions.checkNotNull(forcedState, "forcedState cannot be null");
        this.healthyState = Preconditions.checkNotNull(healthyState, "healthyState cannot be null");
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.adminPort = adminPort;
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    public int getAdminPort()
    {
        return adminPort;
    }

    public void setAdminPort(int adminPort)
    {
        this.adminPort = adminPort;
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
        if ( forcedState != payload.forcedState )
        {
            return false;
        }
        if ( healthyState != payload.healthyState )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !metaData.equals(payload.metaData) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = adminPort;
        result = 31 * result + metaData.hashCode();
        result = 31 * result + forcedState.hashCode();
        result = 31 * result + healthyState.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Payload{" +
            "adminPort=" + adminPort +
            ", metaData=" + metaData +
            ", forcedState=" + forcedState +
            ", healthyState=" + healthyState +
            '}';
    }
}
