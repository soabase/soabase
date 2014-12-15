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

import com.google.common.io.CharStreams;
import io.soabase.client.SoaClientBundle;
import io.soabase.client.SoaRequestId;
import io.soabase.core.SoaFeatures;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Path("/helloapache")
public class HelloResourceApache
{
    private final HttpClient client;

    @Inject
    public HelloResourceApache(@Named(SoaFeatures.DEFAULT_NAME) HttpClient client)
    {
        this.client = client;
    }

    @GET
    public String getHello() throws Exception
    {
        URI uri = new URIBuilder().setHost(SoaClientBundle.HOST_SUBSTITUTION_TOKEN + "GoodbyeApp").setPath("/goodbye").build();
        HttpGet get = new HttpGet(uri);
        ResponseHandler<String> responseHandler = new ResponseHandler<String>()
        {
            @Override
            public String handleResponse(HttpResponse response) throws IOException
            {
                return CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            }
        };
        String value = client.execute(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()), get, responseHandler);
        return "hello - " + SoaRequestId.get() + "\n" + value;
    }
}
