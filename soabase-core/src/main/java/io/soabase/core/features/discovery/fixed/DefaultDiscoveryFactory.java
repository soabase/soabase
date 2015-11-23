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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.discovery.Discovery;
import io.soabase.core.features.discovery.DiscoveryFactory;
import javax.validation.Valid;
import java.util.List;

@JsonTypeName("default")
public class DefaultDiscoveryFactory implements DiscoveryFactory
{
    @Valid
    public List<Instance> instances = Lists.newArrayList();

    @Override
    public Discovery build(Configuration configuration, Environment environment, final SoaInfo soaInfo)
    {
        return new DefaultDiscovery(soaInfo, instances);
    }

}
