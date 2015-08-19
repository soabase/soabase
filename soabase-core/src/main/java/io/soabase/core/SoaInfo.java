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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import java.text.DateFormat;
import java.util.List;
import java.util.TimeZone;

public class SoaInfo
{
    private final List<String> scopes;
    private final HostAndPort mainPort;
    private final HostAndPort adminPort;
    private final String serviceName;
    private final String instanceName;
    private final long startTimeMs = System.currentTimeMillis();
    private final boolean registerInDiscovery;

    public SoaInfo(List<String> scopes, HostAndPort mainPort, HostAndPort adminPort, String serviceName, String instanceName, boolean registerInDiscovery)
    {
        this.registerInDiscovery = registerInDiscovery;
        scopes = Preconditions.checkNotNull(scopes, "scopes cannot be null");
        this.scopes = ImmutableList.copyOf(scopes);
        this.mainPort = Preconditions.checkNotNull(mainPort, "mainPort cannot be null");
        this.adminPort = Preconditions.checkNotNull(adminPort, "adminPort cannot be null");
        this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        this.instanceName = Preconditions.checkNotNull(instanceName, "instanceName cannot be null");
    }

    public List<String> getScopes()
    {
        return scopes;
    }

    public HostAndPort getMainPort()
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

    public HostAndPort getAdminPort()
    {
        return adminPort;
    }

    public boolean isRegisterInDiscovery()
    {
        return registerInDiscovery;
    }

    public static DateFormat newUtcFormatter()
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        ISO8601DateFormat df = new ISO8601DateFormat();
        df.setTimeZone(tz);
        return df;
    }
}
