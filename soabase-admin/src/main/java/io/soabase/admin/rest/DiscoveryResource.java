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
package io.soabase.admin.rest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.Discovery;
import io.soabase.core.features.discovery.DiscoveryInstance;
import io.soabase.core.features.discovery.ExtendedDiscovery;
import io.soabase.core.features.discovery.ForcedState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("/soa/discovery")
public class DiscoveryResource
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SoaFeatures features;

    @Inject
    public DiscoveryResource(SoaFeatures features)
    {
        this.features = features;
    }

    @GET
    @Path("deploymentGroups/{serviceName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeploymentGroups(@PathParam("serviceName") String serviceName)
    {
        Map<String, Boolean> groupStates = Maps.newTreeMap();
        for ( String group : features.getDeploymentGroupManager().getKnownGroups(serviceName) )
        {
            groupStates.put(group, features.getDeploymentGroupManager().isGroupEnabled(serviceName, group));
        }
        GenericEntity<Map<String, Boolean>> entity = new GenericEntity<Map<String, Boolean>>(groupStates){};
        return Response.ok(entity).build();
    }

    @PUT
    @Path("deploymentGroup/{serviceName}/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ableGroup(@PathParam("serviceName") String serviceName, @PathParam("name") String groupName, boolean enable)
    {
        features.getDeploymentGroupManager().ableGroup(serviceName, groupName, enable);
        return Response.ok().build();
    }

    @GET
    @Path("services")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getServiceNames()
    {
        ExtendedDiscovery discovery = getDiscovery();
        Collection<String> names;
        try
        {
            names = discovery.queryForServiceNames();
        }
        catch ( Exception e )
        {
            log.error("Could not retrieve service names", e);
            names = Sets.newHashSet();
        }
        List<String> services = Lists.newArrayList(names);
        Collections.sort(services);
        return services;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all/{name}")
    public Collection<DiscoveryInstance> getInstances(@PathParam("name") String serviceName)
    {
        ExtendedDiscovery discovery = getDiscovery();
        List<DiscoveryInstance> instances = Lists.newArrayList(discovery.queryForAllInstances(serviceName));
        Collections.sort(instances);
        return instances;
    }

    @PUT
    @Path("force/{name}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setForce(@PathParam("name") String serviceName, @PathParam("id") String instanceId, ForcedState forcedState)
    {
        getDiscovery().setForcedState(serviceName, instanceId, forcedState);
        return Response.ok().build();
    }

    private ExtendedDiscovery getDiscovery()
    {
        Discovery discovery = features.getDiscovery();
        if ( discovery instanceof ExtendedDiscovery )
        {
            return (ExtendedDiscovery)discovery;
        }

        String message = "Discovery instance is not extended";
        log.error(message);
        throw new WebApplicationException(message);
    }
}
