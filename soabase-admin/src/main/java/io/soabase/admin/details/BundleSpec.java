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
package io.soabase.admin.details;

import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;

public class BundleSpec<T extends Configuration>
{
    private final Bundle bundle;
    private final Phase phase;
    private final ConfiguredBundle<T> configuredBundle;

    public enum Phase
    {
        PRE_SOA,
        POST_SOA
    }

    public BundleSpec(Bundle bundle, Phase phase)
    {
        this.bundle = bundle;
        this.configuredBundle = null;
        this.phase = phase;
    }

    public BundleSpec(ConfiguredBundle<T> configuredBundle, Phase phase)
    {
        this.bundle = null;
        this.configuredBundle = configuredBundle;
        this.phase = phase;
    }

    public Bundle getBundle()
    {
        return bundle;
    }

    public Phase getPhase()
    {
        return phase;
    }

    public ConfiguredBundle<T> getConfiguredBundle()
    {
        return configuredBundle;
    }
}
