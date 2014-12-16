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
package io.soabase.example.hello;

import io.soabase.client.SoaClientBundle;
import io.soabase.client.SoaRequestId;
import io.soabase.core.SoaFeatures;
import io.soabase.example.goodbye.GoodbyeResource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/hello")
public class HelloResourceJersey
{
    private final Client client;

    @Inject
    public HelloResourceJersey(@Named(SoaFeatures.DEFAULT_NAME) Client client)
    {
        this.client = client;
    }

    @GET
    public String getHello() throws Exception
    {
        URI uri = UriBuilder.fromResource(GoodbyeResource.class).host(SoaClientBundle.HOST_SUBSTITUTION_TOKEN + "goodbye").build();
        String value = client.target(uri).request().get(String.class);
        return "hello - " + SoaRequestId.get() + "\n" + value;
    }
}
