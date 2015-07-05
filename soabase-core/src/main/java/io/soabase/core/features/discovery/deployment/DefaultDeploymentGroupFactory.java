package io.soabase.core.features.discovery.deployment;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaBundle;
import io.soabase.core.features.config.ComposedConfigurationAccessor;
import io.soabase.core.features.config.SoaConfiguration;

@JsonTypeName("default")
public class DefaultDeploymentGroupFactory implements DeploymentGroupFactory
{
    @Override
    public DeploymentGroupManager build(Configuration configuration, Environment environment)
    {
        SoaConfiguration soaConfiguration = ComposedConfigurationAccessor.access(configuration, environment, SoaConfiguration.class);
        return new DefaultDeploymentGroupManager(SoaBundle.getFeatures(environment).getAttributes(), soaConfiguration.getDeploymentGroups());
    }
}
