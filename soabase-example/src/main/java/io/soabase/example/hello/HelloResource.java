package io.soabase.example.hello;

import com.google.common.io.CharStreams;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
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
    private final SoaFeatures features;
    private final HttpClient client;

    @Inject
    public HelloResource(SoaFeatures features, @Named(SoaFeatures.DEFAULT_NAME) HttpClient client)
    {
        this.features = features;
        this.client = client;
    }

    @GET
    public String getHello() throws Exception
    {
        SoaDiscoveryInstance instance = features.getDiscovery().getInstance("GoodbyeApp");
        URI uri = new URIBuilder().setScheme("http").setHost(instance.getHost()).setPort(instance.getPort()).setPath("/goodbye").build();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = client.execute(get);
        String value = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

        return "hello/" + value;
    }
}
