package io.soabase.admin.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;

public class ComponentManager
{
    private final List<TabComponent> tabs = Lists.newCopyOnWriteArrayList();
    private final String appName;

    public ComponentManager(String appName)
    {
        this.appName = appName;
    }

    public void addTab(final TabComponent tab)
    {
        Preconditions.checkArgument(!tabs.contains(tab), "There is already a tab with the id: " + tab.getId());
        tabs.add(tab);
    }

    public List<TabComponent> getTabs()
    {
        return tabs;
    }

    public String getAppName()
    {
        return appName;
    }
}
