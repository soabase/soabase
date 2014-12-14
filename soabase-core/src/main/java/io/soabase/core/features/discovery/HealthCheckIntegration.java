package io.soabase.core.features.discovery;

import com.codahale.metrics.health.HealthCheckRegistry;
import java.util.concurrent.atomic.AtomicBoolean;

public class HealthCheckIntegration implements Runnable
{
    private final HealthCheckRegistry registry;
    private final SoaDiscovery discovery;
    private final SoaDiscoveryHealth health;

    public HealthCheckIntegration(HealthCheckRegistry registry, SoaDiscovery discovery, SoaDiscoveryHealth health)
    {
        this.registry = registry;
        this.discovery = discovery;
        this.health = health;
    }

    @Override
    public void run()
    {
        discovery.setHealthyState(health.shouldBeInDiscovery(registry) ? SoaDiscovery.HealthyState.HEALTHY : SoaDiscovery.HealthyState.UNHEALTHY);
    }
}
