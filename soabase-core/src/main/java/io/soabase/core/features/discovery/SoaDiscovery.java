package io.soabase.core.features.discovery;

public interface SoaDiscovery
{
    public SoaDiscoveryInstance getInstance(String serviceName);

    public void noteError(String serviceName, SoaDiscoveryInstance errorInstance);

    public void addThisInstance();

    public void removeThisInstance();
}
