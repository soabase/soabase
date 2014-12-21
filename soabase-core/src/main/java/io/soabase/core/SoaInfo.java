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
package io.soabase.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class SoaInfo
{
    private final List<String> scopes;
    private final int mainPort;
    private final int adminPort;
    private final String serviceName;
    private final String instanceName;
    private final long startTimeMs = System.currentTimeMillis();
    private final boolean registerInDiscovery;

    public SoaInfo(List<String> scopes, int mainPort, int adminPort, String serviceName, String instanceName, boolean registerInDiscovery)
    {
        this.registerInDiscovery = registerInDiscovery;
        scopes = Preconditions.checkNotNull(scopes, "scopes cannot be null");
        this.scopes = ImmutableList.copyOf(scopes);
        this.mainPort = mainPort;
        this.adminPort = adminPort;
        this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        this.instanceName = Preconditions.checkNotNull(instanceName, "instanceName cannot be null");
    }

    public List<String> getScopes()
    {
        return scopes;
    }

    public int getMainPort()
    {
        return mainPort;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public long getStartTimeMs()
    {
        return startTimeMs;
    }

    public int getAdminPort()
    {
        return adminPort;
    }

    public boolean isRegisterInDiscovery()
    {
        return registerInDiscovery;
    }
}
