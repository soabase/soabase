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
package io.soabase.core.rest.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class Instance
{
    private String host;
    private int port;
    private boolean forceSsl;
    private int adminPort;
    private Map<String, String> metaData;

    public Instance()
    {
        this("", 0, 0, false, Maps.<String, String>newHashMap());
    }

    public Instance(String host, int port, int adminPort, boolean forceSsl, Map<String, String> metaData)
    {
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.adminPort = adminPort;
        this.metaData = ImmutableMap.copyOf(metaData);
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
        this.forceSsl = forceSsl;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public boolean isForceSsl()
    {
        return forceSsl;
    }

    public void setForceSsl(boolean forceSsl)
    {
        this.forceSsl = forceSsl;
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
        this.metaData = ImmutableMap.copyOf(metaData);
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

        Instance instance = (Instance)o;

        if ( adminPort != instance.adminPort )
        {
            return false;
        }
        if ( forceSsl != instance.forceSsl )
        {
            return false;
        }
        if ( port != instance.port )
        {
            return false;
        }
        if ( !host.equals(instance.host) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !metaData.equals(instance.metaData) )
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
        return "Instance{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            ", adminPort=" + adminPort +
            ", metaData=" + metaData +
            '}';
    }
}
