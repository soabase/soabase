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
package io.soabase.admin.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;

public class ComponentManager
{
    private final List<TabComponent> tabs = Lists.newCopyOnWriteArrayList();
    private final String appName;
    private final String companyName;
    private final String footerMessage;

    public ComponentManager(String appName, String companyName, String footerMessage)
    {
        this.appName = appName;
        this.companyName = companyName;
        this.footerMessage = footerMessage;
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

    public String getCompanyName()
    {
        return companyName;
    }

    public String getFooterMessage()
    {
        return footerMessage;
    }
}
