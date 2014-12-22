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
package io.soabase.example;

import io.dropwizard.Configuration;
import io.soabase.client.SoaClientConfiguration;
import io.soabase.core.SoaConfiguration;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorConfiguration;

public class ExampleConfiguration extends Configuration
{
    public SoaConfiguration soa = new SoaConfiguration();
    public SqlConfiguration sql = new SqlConfiguration();
    public CuratorConfiguration curator = new CuratorConfiguration();
    public SoaClientConfiguration client = new SoaClientConfiguration();
}
