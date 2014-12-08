package io.soabase.core.features.discovery;

import com.codahale.metrics.health.HealthCheckRegistry;

public interface SoaDiscoveryHealth
{
    public boolean shouldBeInDiscovery(HealthCheckRegistry registry);
}
