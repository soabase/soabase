package io.soabase.core.rest;

import com.google.common.collect.Lists;
import io.soabase.core.SoaFeatures;
import io.soabase.core.rest.entities.Attribute;
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
import java.util.Collections;
import java.util.List;

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
    public List<Attribute> getAll()
    {
        List<Attribute> attributes = Lists.newArrayList(features.getAttributes());
        Collections.sort(attributes);
        return attributes;
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
