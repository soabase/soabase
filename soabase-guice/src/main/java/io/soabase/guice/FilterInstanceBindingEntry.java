package io.soabase.guice;

import com.google.inject.Key;
import javax.servlet.Filter;

class FilterInstanceBindingEntry
{
    final Key<Filter> key;
    final Filter filter;

    FilterInstanceBindingEntry(Key<Filter> key, Filter filter)
    {
        this.key = key;
        this.filter = filter;
    }
}
