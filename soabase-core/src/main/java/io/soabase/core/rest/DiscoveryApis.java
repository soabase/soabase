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
package io.soabase.core.rest;

import com.google.common.collect.Lists;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.ForcedState;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

@Path("/soa/discovery")
public class DiscoveryApis
{
    private final SoaFeatures features;

    @Inject
    public DiscoveryApis(SoaFeatures features)
    {
        this.features = features;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{name}")
    public Response getInstance(@PathParam("name") String serviceName)
    {
        // TODO logging

        SoaDiscoveryInstance instance = features.getDiscovery().getInstance(serviceName);
        if ( instance == null )
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(instance).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all/{name}")
    public Collection<SoaDiscoveryInstance> getInstances(@PathParam("name") String serviceName)
    {
        return features.getDiscovery().getAllInstances(serviceName);
    }

    @GET
    @Path("services")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getServiceNames()
    {
        return Lists.newArrayList(features.getDiscovery().getServiceNames());
    }
}
