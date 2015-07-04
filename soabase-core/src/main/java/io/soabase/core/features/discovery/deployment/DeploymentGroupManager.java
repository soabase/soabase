package io.soabase.core.features.discovery.deployment;

import java.util.Collection;

public interface DeploymentGroupManager
{
    void ableGroup(String groupName, boolean enable);

    boolean isGroupEnabled(String groupName);

    boolean isAnyGroupEnabled(Collection<String> groups);

    Collection<String> getKnownGroups();
}
