package io.soabase.core.features.discovery;

import java.util.Map;

public interface SoaDiscoveryInstance
{
    public String getHost();

    public int getPort();

    public boolean isForceSsl();

    public int getAdminPort();

    public HealthyState getHealthyState();

    public Map<String, String> getMetaData();

    public ForcedState getForcedState();
}
