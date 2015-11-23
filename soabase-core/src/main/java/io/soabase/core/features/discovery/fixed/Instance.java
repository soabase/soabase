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

import com.google.common.base.Preconditions;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Instance
{
    @NotNull
    private String serviceName;

    @NotNull
    private String host;

    @Min(1)
    private int port;

    @Min(0)
    private int adminPort;

    public Instance()
    {
        this("", "", 1, 0);
    }

    public Instance(String serviceName, String host, int port, int adminPort)
    {
        this.adminPort = adminPort;
        this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public int getAdminPort()
    {
        return adminPort;
    }

    public void setAdminPort(int adminPort)
    {
        this.adminPort = adminPort;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public void setPort(int port)
    {
        this.port = port;
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

        if ( port != instance.port )
        {
            return false;
        }
        if ( adminPort != instance.adminPort )
        {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if ( !serviceName.equals(instance.serviceName) )
        {
            return false;
        }
        return host.equals(instance.host);

    }

    @Override
    public int hashCode()
    {
        int result = serviceName.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + adminPort;
        return result;
    }

    @Override
    public String toString()
    {
        return "Instance{" +
            "serviceName='" + serviceName + '\'' +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", adminPort=" + adminPort +
            '}';
    }
}
