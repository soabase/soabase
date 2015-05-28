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
package io.soabase.core.features.discovery;

import java.util.Collection;
import java.util.Map;

public interface Discovery
{
    public Collection<String> getServiceNames();

    public DiscoveryInstance getInstance(String serviceName);

    public Collection<DiscoveryInstance> getAllInstances(String serviceName);

    public void noteError(String serviceName, DiscoveryInstance errorInstance, int statusCode, Throwable exception);

    public void noteSuccess(String serviceName, DiscoveryInstance instance);

    public void setHealthyState(HealthyState healthyState);

    public void setMetaData(Map<String, String> newMetaData);
}
