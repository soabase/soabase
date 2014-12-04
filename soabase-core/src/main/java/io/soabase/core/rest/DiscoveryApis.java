package io.soabase.core.rest;

import io.soabase.core.features.SoaFeatures;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
    public String get()
    {
        return "hey";
    }
}
