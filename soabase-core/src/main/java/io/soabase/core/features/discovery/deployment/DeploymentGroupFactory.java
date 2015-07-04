package io.soabase.core.features.discovery.deployment;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultDeploymentGroupFactory.class)
public interface DeploymentGroupFactory
{
    DeploymentGroupManager build(Configuration configuration, Environment environment);
}
