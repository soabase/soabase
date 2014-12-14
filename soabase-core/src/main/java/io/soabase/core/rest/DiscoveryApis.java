package io.soabase.core.rest;

import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.SoaDiscovery;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/soa/discovery")
public class DiscoveryApis
{
    private final SoaFeatures features;

    @Inject
    public DiscoveryApis(SoaFeatures features)
    {
        this.features = features;
    }

    @PUT
    @Path("force")
    public Response forceRegister(ForceType forceType)
    {
        // TODO logging
        SoaDiscovery.ForcedState state = forceType.isRegister() ? SoaDiscovery.ForcedState.REGISTER : SoaDiscovery.ForcedState.UNREGISTER;
        features.getDiscovery().setForcedState(state);

        return Response.ok().build();
    }

    @DELETE
    @Path("force")
    public Response forceClear()
    {
        // TODO logging
        features.getDiscovery().setForcedState(SoaDiscovery.ForcedState.CLEARED);

        return Response.ok().build();
    }
}
