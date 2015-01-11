package io.soabase.core.features.attributes;

import com.google.common.collect.Lists;
import java.util.List;

class RecordingListener implements DynamicAttributeListener
{
    private final List<ListenerEntry> entries = Lists.newArrayList();

    public void attributeChanged(String key, String scope)
    {
        entries.add(new ListenerEntry("attributeChanged", key, scope));
    }

    @Override
    public void attributeAdded(String key, String scope)
    {
        entries.add(new ListenerEntry("attributeAdded", key, scope));
    }

    @Override
    public void attributeRemoved(String key, String scope)
    {
        entries.add(new ListenerEntry("attributeRemoved", key, scope));
    }

    @Override
    public void overrideAdded(String key)
    {
        entries.add(new ListenerEntry("overrideAdded", key, ""));
    }

    @Override
    public void overrideRemoved(String key)
    {
        entries.add(new ListenerEntry("overrideRemoved", key, ""));
    }

    public List<ListenerEntry> getEntries()
    {
        return Lists.newArrayList(entries);
    }

    public void clear()
    {
        entries.clear();
    }
}
