package io.soabase.core.features.discovery;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DefaultDiscoveryHealth implements SoaDiscoveryHealth
{
    @Override
    public boolean shouldBeInDiscovery(HealthCheckRegistry registry)
    {
        return Iterables.all(registry.runHealthChecks().values(), new Predicate<HealthCheck.Result>()
        {
            @Override
            public boolean apply(HealthCheck.Result result)
            {
                return result.isHealthy();
            }
        });
    }
}
