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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;

public class Info
{
    private List<String> scopes;
    private HostAndPort mainPort;
    private HostAndPort adminPort;
    private String serviceName;
    private String instanceName;
    private String startTimeUtc;
    private String currentTimeUtc;

    public Info()
    {
        this(Lists.<String>newArrayList(), new HostAndPort(), new HostAndPort(), "", "", "", "");
    }

    public Info(List<String> scopes, HostAndPort mainPort, HostAndPort adminPort, String serviceName, String instanceName, String startTimeUtc, String currentTimeUtc)
    {
        scopes = Preconditions.checkNotNull(scopes, "scopes cannot be null");
        this.scopes = ImmutableList.copyOf(scopes);
        this.mainPort = Preconditions.checkNotNull(mainPort, "mainPort cannot be null");
        this.adminPort = Preconditions.checkNotNull(adminPort, "adminPort cannot be null");
        this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        this.instanceName = Preconditions.checkNotNull(instanceName, "instanceName cannot be null");
        this.startTimeUtc = Preconditions.checkNotNull(startTimeUtc, "startTimeUtc cannot be null");
        this.currentTimeUtc = Preconditions.checkNotNull(currentTimeUtc, "currentTimeUtc cannot be null");
    }

    public List<String> getScopes()
    {
        return scopes;
    }

    public void setScopes(List<String> scopes)
    {
        this.scopes = scopes;
    }

    public HostAndPort getMainPort()
    {
        return mainPort;
    }

    public void setMainPort(HostAndPort mainPort)
    {
        this.mainPort = mainPort;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }

    public String getStartTimeUtc()
    {
        return startTimeUtc;
    }

    public void setStartTimeUtc(String startTimeUtc)
    {
        this.startTimeUtc = startTimeUtc;
    }

    public String getCurrentTimeUtc()
    {
        return currentTimeUtc;
    }

    public void setCurrentTimeUtc(String currentTimeUtc)
    {
        this.currentTimeUtc = currentTimeUtc;
    }

    public HostAndPort getAdminPort()
    {
        return adminPort;
    }

    public void setAdminPort(HostAndPort adminPort)
    {
        this.adminPort = adminPort;
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

        Info info = (Info)o;

        if ( adminPort != info.adminPort )
        {
            return false;
        }
        if ( mainPort != info.mainPort )
        {
            return false;
        }
        if ( !currentTimeUtc.equals(info.currentTimeUtc) )
        {
            return false;
        }
        if ( !instanceName.equals(info.instanceName) )
        {
            return false;
        }
        if ( !scopes.equals(info.scopes) )
        {
            return false;
        }
        if ( !serviceName.equals(info.serviceName) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !startTimeUtc.equals(info.startTimeUtc) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = scopes.hashCode();
        result = 31 * result + mainPort.hashCode();
        result = 31 * result + adminPort.hashCode();
        result = 31 * result + serviceName.hashCode();
        result = 31 * result + instanceName.hashCode();
        result = 31 * result + startTimeUtc.hashCode();
        result = 31 * result + currentTimeUtc.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Info{" +
            "scopes=" + scopes +
            ", mainPort=" + mainPort +
            ", adminPort=" + adminPort +
            ", serviceName='" + serviceName + '\'' +
            ", instanceName='" + instanceName + '\'' +
            ", startTimeUtc='" + startTimeUtc + '\'' +
            ", currentTimeUtc='" + currentTimeUtc + '\'' +
            '}';
    }
}
