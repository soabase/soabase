package io.soabase.core.features.discovery;

import java.util.Collection;

public interface SoaExtendedDiscovery extends SoaDiscovery
{
    public Collection<String> queryForServiceNames();

    public Collection<SoaDiscoveryInstance> queryForAllInstances(String serviceName);

    public void setForcedState(String serviceName, String instanceId, ForcedState forcedState);
}
