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
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.HealthyState;
import io.soabase.core.features.discovery.DiscoveryInstance;
import java.util.Collection;
import java.util.Map;

public class DiscoveryInstanceImpl implements DiscoveryInstance
{
    private final String host;
    private final int port;
    private final boolean forceSsl;
    private final Payload payload;
    private final String id;

    public DiscoveryInstanceImpl(String id, String host, int port, boolean forceSsl, Payload payload)
    {
        this.id = Preconditions.checkNotNull(id, "id cannot be null");
        this.payload = Preconditions.checkNotNull(payload, "payload cannot be null");
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
        this.forceSsl = forceSsl;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getHost()
    {
        return host;
    }

    @Override
    public int getPort()
    {
        return port;
    }

    @Override
    public boolean isForceSsl()
    {
        return forceSsl;
    }

    @Override
    public int getAdminPort()
    {
        return payload.getAdminPort();
    }

    @Override
    public Map<String, String> getMetaData()
    {
        return payload.getMetaData();
    }

    @Override
    public HealthyState getHealthyState()
    {
        return payload.getHealthyState();
    }

    @Override
    public ForcedState getForcedState()
    {
        return payload.getForcedState();
    }

    @Override
    public Collection<String> getDeploymentGroups()
    {
        return null;
    }

    @Override
    public int compareTo(DiscoveryInstance o)
    {
        if ( o == null )
        {
            return -1;
        }
        if ( this.equals(o) )
        {
            return 0;
        }
        int diff = host.compareTo(o.getHost());
        if ( diff == 0 )
        {
            diff = port - o.getPort();
        }
        return diff;
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

        DiscoveryInstanceImpl that = (DiscoveryInstanceImpl)o;

        if ( forceSsl != that.forceSsl )
        {
            return false;
        }
        if ( port != that.port )
        {
            return false;
        }
        if ( !host.equals(that.host) )
        {
            return false;
        }
        if ( !id.equals(that.id) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !payload.equals(that.payload) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (forceSsl ? 1 : 0);
        result = 31 * result + payload.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "DiscoveryInstanceImpl{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            ", payload=" + payload +
            ", id='" + id + '\'' +
            '}';
    }
}
