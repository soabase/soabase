package io.soabase.example.hello;

import com.google.common.io.CharStreams;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.InputStreamReader;
import java.net.URI;

@Path("/hello")
public class HelloResource
{
    private final SoaFeatures features;

    @Inject
    public HelloResource(SoaFeatures features)
    {
        this.features = features;
    }

    @GET
    public String getHello() throws Exception
    {
        SoaDiscoveryInstance instance = features.getDiscovery().getInstance("GoodbyeApp");
        HttpClient client = features.getNamed(HttpClient.class, SoaFeatures.DEFAULT_NAME);
        URI uri = new URIBuilder().setScheme("http").setHost(instance.getHost()).setPort(instance.getPort()).setPath("/goodbye").build();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = client.execute(get);
        String value = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

        return "hello/" + value;
    }
}
