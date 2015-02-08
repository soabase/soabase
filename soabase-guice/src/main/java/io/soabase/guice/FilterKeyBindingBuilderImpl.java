package io.soabase.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;
import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

class FilterKeyBindingBuilderImpl implements FilterKeyBindingBuilder
{
    private final JerseyGuiceModule module;
    private final List<String> uriPatterns;

    FilterKeyBindingBuilderImpl(JerseyGuiceModule module, List<String> uriPatterns)
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
        module.add(new FilterInstanceBindingEntry(filterKey, filter));
        through(filterKey, initParams, filter);
    }

    private void through(Key<? extends Filter> filterKey, Map<String, String> initParams, Filter filterInstance)
    {
        module.add(new FilterDefinition(uriPatterns, filterKey, initParams, filterInstance));
    }
}
