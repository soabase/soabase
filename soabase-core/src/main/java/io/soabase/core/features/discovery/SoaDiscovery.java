package io.soabase.core.features.discovery;

public interface SoaDiscovery
{
    public SoaDiscoveryInstance getInstance(String serviceName);

    void noteError(String serviceName, SoaDiscoveryInstance errorInstance);
}
