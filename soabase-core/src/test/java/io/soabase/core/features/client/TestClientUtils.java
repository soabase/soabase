package io.soabase.core.features.client;

import io.soabase.core.features.discovery.DiscoveryInstance;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.net.URI;
import java.net.URISyntaxException;

public class TestClientUtils
{
    @Test
    public void testBackAndForth()
    {
        String[] testServiceNames =
        {
            "one",
            "one-two",
            "one_two",
            "12",
            "x_Y_z",
            "a.b.c",
            ClientUtils.HOST_SUBSTITUTION_TOKEN
        };

        for ( String service : testServiceNames )
        {
            String host = ClientUtils.serviceNameToHost(service);
            Assert.assertEquals(ClientUtils.hostToServiceName(host), service);
        }
    }

    @Test
    public void testApplyToUri() throws URISyntaxException
    {
        URI uri = new URI("http://www.apple.com");
        Assert.assertEquals(ClientUtils.applyToUri(uri, null), uri);

        DiscoveryInstance instance = Mockito.mock(DiscoveryInstance.class);
        Mockito.when(instance.isForceSsl()).thenReturn(false);
        Mockito.when(instance.getHost()).thenReturn("test");
        Mockito.when(instance.getPort()).thenReturn(100);
        Assert.assertEquals(ClientUtils.applyToUri(uri, instance), new URI("http://test:100"));

        Mockito.when(instance.isForceSsl()).thenReturn(true);
        Assert.assertEquals(ClientUtils.applyToUri(uri, instance), new URI("https://test:100"));
    }
}
