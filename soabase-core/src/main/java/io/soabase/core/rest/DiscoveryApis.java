package io.soabase.core.rest;

import io.soabase.core.SoaConfiguration;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/soa/discovery")
public class DiscoveryApis
{
    private final SoaConfiguration configuration;

    @Inject
    public DiscoveryApis(SoaConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @GET
    public String get()
    {
        return "hey";
    }
}
