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
package io.soabase.admin.rest;

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
