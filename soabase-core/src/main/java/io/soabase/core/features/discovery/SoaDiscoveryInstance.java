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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

public class SoaDiscoveryInstance
{
    private final String host;
    private final int port;
    private final boolean forceSsl;
    private final int adminPort;
    private final Map<String, String> metaData;

    public SoaDiscoveryInstance(String host, int port, int adminPort, boolean forceSsl)
    {
        this(host, port, adminPort, forceSsl, Maps.<String, String>newHashMap());
    }

    public SoaDiscoveryInstance(String host, int port, int adminPort, boolean forceSsl, Map<String, String> metaData)
    {
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.metaData = ImmutableMap.copyOf(metaData);
        this.adminPort = adminPort;
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
        this.forceSsl = forceSsl;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public boolean isForceSsl()
    {
        return forceSsl;
    }

    public int getAdminPort()
    {
        return adminPort;
    }

    public Map<String, String> getMetaData()
    {
        return metaData;
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

        SoaDiscoveryInstance that = (SoaDiscoveryInstance)o;

        if ( adminPort != that.adminPort )
        {
            return false;
        }
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
        if ( !metaData.equals(that.metaData) )
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
        result = 31 * result + adminPort;
        result = 31 * result + metaData.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "SoaDiscoveryInstance{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            ", adminPort=" + adminPort +
            ", metaData=" + metaData +
            '}';
    }
}
