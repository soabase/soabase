package io.soabase.core.features.discovery;

import java.util.Collection;

public class SafeSoaDiscovery implements SoaDiscovery
{
    private final SoaDiscovery implementation;

    public SafeSoaDiscovery(SoaDiscovery implementation)
    {
        this.implementation = implementation;
    }

    @Override
    public Collection<String> getServiceNames()
    {
        return implementation.getServiceNames();
    }

    @Override
    public SoaDiscoveryInstance getInstance(String serviceName)
    {
        return implementation.getInstance(serviceName);
    }

    @Override
    public Collection<SoaDiscoveryInstance> getAllInstances(String serviceName)
    {
        return implementation.getAllInstances(serviceName);
    }

    @Override
    public void noteError(String serviceName, SoaDiscoveryInstance errorInstance)
    {
        implementation.noteError(serviceName, errorInstance);
    }

    @Override
    public void setHealthyState(HealthyState healthyState)
    {
        implementation.setHealthyState(healthyState);
    }
}
