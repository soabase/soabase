package io.soabase.example.hello;

import com.google.common.io.CharStreams;
import io.soabase.client.SoaClientBundle;
import io.soabase.core.SoaFeatures;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.InputStreamReader;
import java.net.URI;

@Path("/hello")
public class HelloResource
{
    private final HttpClient client;

    @Inject
    public HelloResource(@Named(SoaFeatures.DEFAULT_NAME) HttpClient client)
    {
        this.client = client;
    }

    @GET
    public String getHello() throws Exception
    {
        URI uri = new URIBuilder().setHost(SoaClientBundle.HOST_SUBSTITUTION_TOKEN + "GoodbyeApp").setPath("/goodbye").build();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = client.execute(get);
        String value = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

        return "hello/" + value;
    }
}
