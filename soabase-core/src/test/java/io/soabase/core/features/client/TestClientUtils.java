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
package io.soabase.core.features.client;

import io.soabase.core.features.discovery.DiscoveryInstance;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
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
            Assert.assertEquals(service, ClientUtils.hostToServiceName(host));
        }
    }

    @Test
    public void testApplyToUri() throws URISyntaxException
    {
        URI uri = new URI("http://www.apple.com");
        Assert.assertEquals(uri, ClientUtils.applyToUri(uri, null));

        DiscoveryInstance instance = Mockito.mock(DiscoveryInstance.class);
        Mockito.when(instance.isForceSsl()).thenReturn(false);
        Mockito.when(instance.getHost()).thenReturn("test");
        Mockito.when(instance.getPort()).thenReturn(100);
        Assert.assertEquals(new URI("http://test:100"), ClientUtils.applyToUri(uri, instance));

        Mockito.when(instance.isForceSsl()).thenReturn(true);
        Assert.assertEquals(new URI("https://test:100"), ClientUtils.applyToUri(uri, instance));
    }

    @Test
    public void testServiceNameToUriForm()
    {
        final String path = "/a/b";
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
            String uriForm = ClientUtils.serviceNameToUriForm(service);
            Assert.assertEquals("//" + ClientUtils.HOST_SUBSTITUTION_TOKEN + service, uriForm);
            uriForm = ClientUtils.serviceNameToUriForm(service, path);
            Assert.assertEquals("//" + ClientUtils.HOST_SUBSTITUTION_TOKEN + service + path, uriForm);
        }
    }
}
