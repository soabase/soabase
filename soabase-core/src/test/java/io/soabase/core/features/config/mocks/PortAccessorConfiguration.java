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
package io.soabase.core.features.config.mocks;

import com.google.common.net.HostAndPort;
import io.dropwizard.Configuration;
import io.soabase.core.SoaMainPortAccessor;

public class PortAccessorConfiguration extends Configuration implements SoaMainPortAccessor<Configuration>
{
    @Override
    public HostAndPort getMainPort(Configuration configuration)
    {
        return HostAndPort.fromParts("x", 1);
    }

    @Override
    public HostAndPort getAdminPort(Configuration configuration)
    {
        return HostAndPort.fromParts("y", 2);
    }
}
