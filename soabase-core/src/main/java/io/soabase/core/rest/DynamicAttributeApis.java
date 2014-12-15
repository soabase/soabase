package io.soabase.core.rest;

import com.google.common.collect.Lists;
import io.soabase.core.SoaFeatures;
import io.soabase.core.rest.entities.Attribute;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
}
