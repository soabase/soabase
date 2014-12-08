package io.soabase.core.features.discovery;

import java.util.Collection;

public interface SoaDiscovery
{
    public SoaDiscoveryInstance getInstance(String serviceName);

    public Collection<SoaDiscoveryInstance> getAllInstances(String serviceName);

    public void noteError(String serviceName, SoaDiscoveryInstance errorInstance);

    public void addThisInstance();

    public void removeThisInstance();
}
