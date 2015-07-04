package io.soabase.core.features.discovery.deployment;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaBundle;

@JsonTypeName("default")
public class DefaultDeploymentGroupFactory implements DeploymentGroupFactory
{
    @Override
    public DeploymentGroupManager build(Configuration configuration, Environment environment)
    {
        return new DefaultDeploymentGroupManager(SoaBundle.getFeatures(environment).getAttributes());
    }
}
