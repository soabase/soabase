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

import com.google.common.net.HostAndPort;
import io.soabase.core.features.config.mocks.MyConfiguration;
import io.soabase.core.features.config.mocks.PortAccessorConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class TestSoaMainPortAccessor
{
    @Test
    public void testNoAccessor()
    {
        SoaBundle.Ports ports = SoaBundle.getPorts(new MyConfiguration());
        Assert.assertEquals(ports.mainPort, HostAndPort.fromParts("localhost", 8080));
        Assert.assertEquals(ports.adminPort, HostAndPort.fromParts("localhost", 8081));
    }

    @Test
    public void testWithAccessor()
    {
        SoaBundle.Ports ports = SoaBundle.getPorts(new PortAccessorConfiguration());
        Assert.assertEquals(ports.mainPort, HostAndPort.fromParts("x", 1));
        Assert.assertEquals(ports.adminPort, HostAndPort.fromParts("y", 2));
    }
}
