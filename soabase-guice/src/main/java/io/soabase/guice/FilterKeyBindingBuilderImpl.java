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
package io.soabase.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;
import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

// heavily copied from Guice Servlet
class FilterKeyBindingBuilderImpl implements FilterKeyBindingBuilder
{
    private final JerseyMultiGuiceModule module;
    private final List<String> uriPatterns;

    FilterKeyBindingBuilderImpl(JerseyMultiGuiceModule module, List<String> uriPatterns)
    {
        this.module = module;
        this.uriPatterns = ImmutableList.copyOf(uriPatterns);
    }

    public void through(Class<? extends Filter> filterKey)
    {
        through(Key.get(filterKey));
    }

    public void through(Key<? extends Filter> filterKey)
    {
        through(filterKey, Maps.<String, String>newHashMap());
    }

    public void through(Filter filter)
    {
        through(filter, Maps.<String, String>newHashMap());
    }

    public void through(Class<? extends Filter> filterKey, Map<String, String> initParams)
    {
        through(Key.get(filterKey), initParams);
    }

    public void through(Key<? extends Filter> filterKey, Map<String, String> initParams)
    {
        through(filterKey, initParams, null);
    }

    public void through(Filter filter, Map<String, String> initParams)
    {
        Key<Filter> filterKey = Key.get(Filter.class, UniqueAnnotations.create());
        module.add(filterKey, filter);
        through(filterKey, initParams, filter);
    }

    private void through(Key<? extends Filter> filterKey, Map<String, String> initParams, Filter filterInstance)
    {
        module.add(new FilterDefinition(uriPatterns, filterKey, initParams, filterInstance));
    }
}
