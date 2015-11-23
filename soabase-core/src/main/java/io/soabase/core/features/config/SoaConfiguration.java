/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.core.features.config;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.dropwizard.validation.ValidationMethod;
import io.soabase.core.features.attributes.DynamicAttributesFactory;
import io.soabase.core.features.attributes.NullDynamicAttributesFactory;
import io.soabase.core.features.discovery.DefaultDiscoveryHealthFactory;
import io.soabase.core.features.discovery.DiscoveryFactory;
import io.soabase.core.features.discovery.DiscoveryHealthFactory;
import io.soabase.core.features.discovery.fixed.DefaultDiscoveryFactory;
import io.soabase.core.features.discovery.deployment.DefaultDeploymentGroupFactory;
import io.soabase.core.features.discovery.deployment.DeploymentGroupFactory;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SoaConfiguration
{
    @NotNull
    private DiscoveryFactory discoveryFactory = new DefaultDiscoveryFactory();

    @NotNull
    private DiscoveryHealthFactory discoveryHealthFactory = new DefaultDiscoveryHealthFactory();

    @NotNull
    private DynamicAttributesFactory attributesFactory = new NullDynamicAttributesFactory();

    @NotNull
    private DeploymentGroupFactory deploymentGroupFactory = new DefaultDeploymentGroupFactory();

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9.]+$", message = "Service Names can only contain letters and numbers")
    private String serviceName;

    @Min(0)
    private int shutdownWaitMaxMs = (int)TimeUnit.MINUTES.toMillis(1);

    private String instanceName;

    @NotNull
    @JsonIgnore
    private List<String> scopes = Lists.newArrayList();

    @JsonIgnore
    private List<String> deploymentGroups = Lists.newArrayList();

    private boolean addCorsFilter = true;

    private boolean registerInDiscovery = true;

    @Min(0)
    private int discoveryHealthCheckPeriodMs = (int)TimeUnit.SECONDS.toMillis(10);

    @NotEmpty
    @Pattern(regexp = "/.*")
    private String adminJerseyPath = "/api";

    @ValidationMethod(message="deploymentGroups cannot be null and names can only contain letters, numbers, underscores, dashes or periods")
    @JsonIgnore
    public boolean isValidDeploymentGroups() {
        if ( deploymentGroups == null )
        {
            return false;
        }
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[a-zA-Z0-9_\\-.]+$");
        for ( String group : deploymentGroups )
        {
            if ( !pattern.matcher(group).matches() )
            {
                return false;
            }
        }
        return true;
    }

    @JsonProperty("checkPeriodMs")
    public int getDiscoveryHealthCheckPeriodMs()
    {
        return discoveryHealthCheckPeriodMs;
    }

    @JsonProperty("checkPeriodMs")
    public void setDiscoveryHealthCheckPeriodMs(int discoveryHealthCheckPeriodMs)
    {
        this.discoveryHealthCheckPeriodMs = discoveryHealthCheckPeriodMs;
    }

    @JsonProperty("discovery")
    public DiscoveryFactory getDiscoveryFactory()
    {
        return discoveryFactory;
    }

    @JsonProperty("discovery")
    public void setDiscoveryFactory(DiscoveryFactory discoveryFactory)
    {
        this.discoveryFactory = discoveryFactory;
    }

    @JsonProperty("attributes")
    public DynamicAttributesFactory getAttributesFactory()
    {
        return attributesFactory;
    }

    @JsonProperty("attributes")
    public void setAttributesFactory(DynamicAttributesFactory attributesFactory)
    {
        this.attributesFactory = attributesFactory;
    }

    @JsonProperty("shutdownWaitMaxMs")
    public int getShutdownWaitMaxMs()
    {
        return shutdownWaitMaxMs;
    }

    @JsonProperty("shutdownWaitMaxMs")
    public void setShutdownWaitMaxMs(int shutdownWaitMaxMs)
    {
        this.shutdownWaitMaxMs = shutdownWaitMaxMs;
    }

    @JsonProperty("instanceName")
    public String getInstanceName()
    {
        return instanceName;
    }

    @JsonProperty("instanceName")
    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }

    public List<String> getScopes()
    {
        return scopes;
    }

    public List<String> getDeploymentGroups()
    {
        return deploymentGroups;
    }

    @JsonAnySetter
    public void setAny(String name, Object value)
    {
        if ( name.equals("deploymentGroups") )
        {
            this.deploymentGroups = toList(value);
        }
        else if ( name.equals("additionalScopes") )
        {
            this.scopes = toList(value);
        }
    }

    @JsonProperty("addCorsFilter")
    public boolean isAddCorsFilter()
    {
        return addCorsFilter;
    }

    @JsonProperty("addCorsFilter")
    public void setAddCorsFilter(boolean addCorsFilter)
    {
        this.addCorsFilter = addCorsFilter;
    }

    @JsonProperty("discoveryHealth")
    public DiscoveryHealthFactory getDiscoveryHealthFactory()
    {
        return discoveryHealthFactory;
    }

    @JsonProperty("discoveryHealth")
    public void setDiscoveryHealthFactory(DiscoveryHealthFactory discoveryHealthFactory)
    {
        this.discoveryHealthFactory = discoveryHealthFactory;
    }

    @JsonProperty("adminJerseyPath")
    public String getAdminJerseyPath()
    {
        return adminJerseyPath;
    }

    @JsonProperty("adminJerseyPath")
    public void setAdminJerseyPath(String adminJerseyPath)
    {
        this.adminJerseyPath = adminJerseyPath;
    }

    @JsonProperty("serviceName")
    public String getServiceName()
    {
        return serviceName;
    }

    @JsonProperty("serviceName")
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    @JsonProperty("registerInDiscovery")
    public boolean isRegisterInDiscovery()
    {
        return registerInDiscovery;
    }

    @JsonProperty("registerInDiscovery")
    public void setRegisterInDiscovery(boolean registerInDiscovery)
    {
        this.registerInDiscovery = registerInDiscovery;
    }

    @JsonProperty("deployment")
    public DeploymentGroupFactory getDeploymentGroupFactory()
    {
        return deploymentGroupFactory;
    }

    @JsonProperty("deployment")
    public void setDeploymentGroupFactory(DeploymentGroupFactory deploymentGroupFactory)
    {
        this.deploymentGroupFactory = deploymentGroupFactory;
    }

    private static List<String> toList(Object o)
    {
        if ( Collection.class.isAssignableFrom(o.getClass()) )
        {
            //noinspection unchecked
            return ImmutableList.copyOf((Collection)o);
        }
        return Splitter.on(',').trimResults().omitEmptyStrings().splitToList(String.valueOf(o));
    }
}
