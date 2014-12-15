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

public class SoaDiscoveryInstance
{
    private final String host;
    private final int port;
    private final boolean forceSsl;

    public SoaDiscoveryInstance(String host, int port, boolean forceSsl)
    {
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

        if ( forceSsl != that.forceSsl )
        {
            return false;
        }
        if ( port != that.port )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !host.equals(that.host) )
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
        return result;
    }

    @Override
    public String toString()
    {
        return "SoaDiscoveryInstance{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            '}';
    }
}
