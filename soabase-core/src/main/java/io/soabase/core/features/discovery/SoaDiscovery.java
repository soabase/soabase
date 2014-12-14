package io.soabase.core.features.discovery;

import java.util.Collection;

public interface SoaDiscovery
{
    public Collection<String> getCurrentServiceNames();

    public SoaDiscoveryInstance getInstance(String serviceName);

    public Collection<SoaDiscoveryInstance> getAllInstances(String serviceName);

    public void noteError(String serviceName, SoaDiscoveryInstance errorInstance);

    public enum HealthyState
    {
        HEALTHY,
        UNHEALTHY
    }

    public void setHealthyState(HealthyState healthyState);

    public HealthyState getHealthyState();

    public enum ForcedState
    {
        REGISTER,
        UNREGISTER,
        CLEARED
    }

    public void setForcedState(ForcedState forcedState);

    public ForcedState getForcedState();
}
