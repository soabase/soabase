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
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import java.util.Map;

public class SoaDiscoveryInstanceImpl implements SoaDiscoveryInstance
{
    private final String host;
    private final int port;
    private final boolean forceSsl;
    private final Payload payload;

    public SoaDiscoveryInstanceImpl(String host, int port, boolean forceSsl, Payload payload)
    {
        this.payload = Preconditions.checkNotNull(payload, "payload cannot be null");
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
        this.forceSsl = forceSsl;
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

        SoaDiscoveryInstanceImpl that = (SoaDiscoveryInstanceImpl)o;

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
        return result;
    }

    @Override
    public String toString()
    {
        return "SoaDiscoveryInstanceImpl{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            ", payload=" + payload +
            '}';
    }
}
