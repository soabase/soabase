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
package io.soabase.guice.example;

import javax.inject.Inject; // important! use javax annotations, not Guice annotations
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/test")
public class ExampleRestResource
{
    private final ExampleInjected map;

    @Inject // important! use javax annotations, not Guice annotations
    public ExampleRestResource(ExampleInjected map)
    {
        this.map = map;
    }

    @GET
    @Path("{key}")
    public String get(@PathParam("key") String key)
    {
        return map.get(key);
    }

    @PUT
    @Path("{key}/{value}")
    public Response put(@PathParam("key") String key, @PathParam("value") String value)
    {
        map.put(key, value);
        return Response.ok().build();
    }
}
