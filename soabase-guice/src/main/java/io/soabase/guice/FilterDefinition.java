package io.soabase.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An internal representation of a filter definition against a particular URI pattern.
 */
class FilterDefinition
{
    private final List<String> uriPatterns;
    private final Key<? extends Filter> filterKey;
    private final Filter filterInstance;
    private final Map<String, String> initParams;

    FilterDefinition(List<String> uriPatterns, Key<? extends Filter> filterKey, Map<String, String> initParams, Filter filterInstance)
    {
        this.uriPatterns = ImmutableList.copyOf(uriPatterns);
        this.filterKey = filterKey;
        this.filterInstance = filterInstance;
        this.initParams = Collections.unmodifiableMap(Maps.newHashMap(initParams));
    }

    String[] getUriPatterns()
    {
        return uriPatterns.toArray(new String[uriPatterns.size()]);
    }

    Key<? extends Filter> getFilterKey()
    {
        return filterKey;
    }

    Filter getFilterInstance()
    {
        return filterInstance;
    }

    Map<String, String> getInitParams()
    {
        return initParams;
    }
}
