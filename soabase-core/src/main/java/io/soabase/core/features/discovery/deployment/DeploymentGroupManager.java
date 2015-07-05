package io.soabase.core.features.discovery.deployment;

import java.util.Collection;

public interface DeploymentGroupManager
{
    void ableGroup(String serviceName, String groupName, boolean enable);

    boolean isGroupEnabled(String serviceName, String groupName);

    boolean isAnyGroupEnabled(String serviceName, Collection<String> groups);

    Collection<String> getKnownGroups(String serviceName);

    Collection<String> getInstanceGroups();
}
