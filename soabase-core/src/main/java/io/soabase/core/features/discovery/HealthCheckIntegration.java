package io.soabase.core.features.discovery;

import com.codahale.metrics.health.HealthCheckRegistry;
import java.util.concurrent.atomic.AtomicBoolean;

public class HealthCheckIntegration implements Runnable
{
    private final HealthCheckRegistry registry;
    private final SoaDiscovery discovery;
    private final SoaDiscoveryHealth health;
    private final AtomicBoolean isInDiscovery = new AtomicBoolean(false);

    public HealthCheckIntegration(HealthCheckRegistry registry, SoaDiscovery discovery, SoaDiscoveryHealth health)
    {
        this.registry = registry;
        this.discovery = discovery;
        this.health = health;
    }

    @Override
    public void run()
    {
        if ( health.shouldBeInDiscovery(registry) )
        {
            if ( isInDiscovery.compareAndSet(false, true) )
            {
                discovery.addThisInstance();
            }
        }
        else
        {
            if ( isInDiscovery.compareAndSet(true, false) )
            {
                discovery.removeThisInstance();
            }
        }
    }
}
