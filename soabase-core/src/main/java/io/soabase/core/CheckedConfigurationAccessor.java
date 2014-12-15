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
import io.dropwizard.Configuration;

public class CheckedConfigurationAccessor<C extends Configuration, T> implements ConfigurationAccessor<C, T>
{
    private final ConfigurationAccessor<C, T> accessor;

    public CheckedConfigurationAccessor(ConfigurationAccessor<C, T> accessor)
    {
        this.accessor = Preconditions.checkNotNull(accessor, "accessor cannot be null");
    }

    @Override
    public T accessConfiguration(C configuration)
    {
        T reference = accessor.accessConfiguration(configuration);
        return Preconditions.checkNotNull(reference, "Could not access configuration via accessor: " + accessor.getClass().getName());
    }
}
