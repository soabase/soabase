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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class Payload
{
    private int adminPort;
    private Map<String, String> metaData;

    public Payload()
    {
        this(0, Maps.<String, String>newHashMap());
    }

    public Payload(int adminPort, Map<String, String> metaData)
    {
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
}
