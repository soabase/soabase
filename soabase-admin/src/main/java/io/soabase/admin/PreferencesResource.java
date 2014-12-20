package io.soabase.admin;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.prefs.Preferences;

@Path("/api/prefs/{key}")
public class PreferencesResource
{
    private final Preferences preferences;

    @Inject
    public PreferencesResource(Preferences preferences)
    {
        this.preferences = preferences;
    }

    @PUT
    public Response add(@PathParam("key") String key, String value)
    {
        preferences.put(key, value);
        return Response.ok().build();
    }

    @DELETE
    public Response delete(@PathParam("key") String key)
    {
        preferences.remove(key);
        return Response.ok().build();
    }

    @GET
    public String get(@PathParam("key") String key)
    {
        return preferences.get(key, "");
    }
}
