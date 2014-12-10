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
        URI uri = UriBuilder.fromResource(GoodbyeResource.class).host(SoaClientBundle.HOST_SUBSTITUTION_TOKEN + "GoodbyeApp").build();
        String value = client.target(uri).request().get(String.class);
        return "hello - " + SoaRequestId.get() + "\n" + value;
    }
}
