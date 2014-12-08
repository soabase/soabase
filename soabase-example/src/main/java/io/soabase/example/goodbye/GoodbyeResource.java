package io.soabase.example.goodbye;

import io.soabase.core.SoaFeatures;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/goodbye")
public class GoodbyeResource
{
    private final SoaFeatures features;

    @Inject
    public GoodbyeResource(SoaFeatures features)
    {
        this.features = features;
    }

    @GET
    public String getHello()
    {
        return "goodbye";
    }
}
