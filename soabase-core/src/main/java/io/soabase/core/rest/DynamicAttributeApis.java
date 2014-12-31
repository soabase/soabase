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

import io.soabase.core.SoaFeatures;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/soa/attributes")
public class DynamicAttributeApis
{
    private final SoaFeatures features;

    @Inject
    public DynamicAttributeApis(SoaFeatures features)
    {
        this.features = features;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{key}")
    public String getAttribute(@PathParam("key") String key)
    {
        return features.getAttributes().getAttribute(key, "");
    }

    @PUT
    @Path("{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOverrideAttribute(@PathParam("key") String key, String value)
    {
        features.getAttributes().temporaryOverride(key, value);
        return Response.ok().build();
    }

    @DELETE
    @Path("{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unsetOverrideAttribute(@PathParam("key") String key)
    {
        features.getAttributes().removeOverride(key);
        return Response.ok().build();
    }
}
