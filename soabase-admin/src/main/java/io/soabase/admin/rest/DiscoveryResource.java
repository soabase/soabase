package io.soabase.admin.rest;

import com.google.common.collect.Lists;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import io.soabase.core.features.discovery.SoaExtendedDiscovery;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Path("/soa/discovery")
public class DiscoveryResource
{
    private final SoaFeatures features;

    @Inject
    public DiscoveryResource(SoaFeatures features)
    {
        this.features = features;
    }

    @GET
    @Path("services")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getServiceNames()
    {
        SoaExtendedDiscovery discovery = getDiscovery();
        List<String> services = Lists.newArrayList(discovery.queryForServiceNames());
        Collections.sort(services);
        return services;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all/{name}")
    public Collection<SoaDiscoveryInstance> getInstances(@PathParam("name") String serviceName)
    {
        SoaExtendedDiscovery discovery = getDiscovery();
        List<SoaDiscoveryInstance> instances = Lists.newArrayList(discovery.queryForAllInstances(serviceName));
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

    private SoaExtendedDiscovery getDiscovery()
    {
        SoaDiscovery discovery = features.getDiscovery();
        if ( discovery instanceof SoaExtendedDiscovery )
        {
            return (SoaExtendedDiscovery)discovery;
        }

        // TODO logging
        throw new WebApplicationException("Discovery instance is not extended");
    }
}
