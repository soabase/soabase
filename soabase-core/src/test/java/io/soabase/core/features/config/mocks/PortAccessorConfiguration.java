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

import io.dropwizard.Configuration;
import io.soabase.core.SoaMainPortAccessor;

public class PortAccessorConfiguration extends Configuration implements SoaMainPortAccessor<Configuration>
{
    @Override
    public int getMainPort(Configuration configuration)
    {
        return 1;
    }

    @Override
    public int getAdminPort(Configuration configuration)
    {
        return 2;
    }
}
