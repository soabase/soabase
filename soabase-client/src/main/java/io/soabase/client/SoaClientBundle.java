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
package io.soabase.client;

import com.google.common.base.Preconditions;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.client.apache.WrappedHttpClient;
import io.soabase.client.jersey.JerseyRetryConnectorProvider;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.client.DefaultRetryHandler;
import io.soabase.core.features.client.RetryComponents;
import io.soabase.core.features.client.RetryHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.ws.rs.client.Client;
import java.io.IOException;

public class SoaClientBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    private final String clientName;

    private volatile RetryHandler retryHandler = new DefaultRetryHandler();

    public SoaClientBundle()
    {
        this(SoaFeatures.DEFAULT_NAME);
    }

    public SoaClientBundle(String clientName)
    {
        this.clientName = Preconditions.checkNotNull(clientName, "clientName cannot be null");
    }

    public RetryHandler getRetryHandler()
    {
        return retryHandler;
    }

    public void setRetryHandler(RetryHandler retryHandler)
    {
        this.retryHandler = retryHandler;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        SoaFeatures features = SoaBundle.getFeatures(environment);
        SoaClientConfiguration clientConfiguration = SoaBundle.access(configuration, environment, SoaClientConfiguration.class);

        RetryComponents retryComponents = new RetryComponents(retryHandler, features.getDiscovery(), clientConfiguration.getMaxRetries(), clientConfiguration.isRetry500s(), features.getExecutorBuilder());

        final HttpClient httpClient = buildHttpClient(configuration, clientConfiguration, environment, retryComponents);
        final Client jerseyClient = buildJerseyClient(configuration, clientConfiguration, environment, retryComponents);

        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                if ( httpClient != null )
                {
                    bind(httpClient).named(clientName).to(HttpClient.class);
                }
                if ( jerseyClient != null )
                {
                    bind(jerseyClient).named(clientName).to(Client.class);
                }
            }
        };
        environment.jersey().register(binder);
    }

    // protected so users can override
    @SuppressWarnings("UnusedParameters")
    protected JerseyClientBuilder updateJerseyClientBuilder(Configuration configuration, Environment environment, JerseyClientBuilder builder)
    {
        return builder;
    }

    // protected so users can override
    @SuppressWarnings("UnusedParameters")
    protected HttpClientBuilder updateHttpClientBuilder(Configuration configuration, Environment environment, HttpClientBuilder httpClientBuilder)
    {
        return httpClientBuilder;
    }

    private Client buildJerseyClient(Configuration configuration, SoaClientConfiguration clientConfiguration, Environment environment, RetryComponents retryComponents)
    {
        JerseyClientConfiguration jerseyClientConfiguration = clientConfiguration.getJerseyClientConfiguration();
        if ( jerseyClientConfiguration == null )
        {
            return null;
        }

        JerseyClientBuilder builder = new JerseyClientBuilder(environment)
            .using(jerseyClientConfiguration)
            .using(new JerseyRetryConnectorProvider(retryComponents));
        builder = updateJerseyClientBuilder(configuration, environment, builder);
        Client client = builder.build(clientName);
        SoaBundle.getFeatures(environment).putNamed(client, Client.class, clientName);

        return client;
    }

    private HttpClient buildHttpClient(Configuration configuration, SoaClientConfiguration clientConfiguration, Environment environment, RetryComponents retryComponents)
    {
        if ( clientConfiguration.getHttpClientConfiguration() == null )
        {
            return null;
        }

        HttpRequestRetryHandler nullRetry = new HttpRequestRetryHandler()
        {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
            {
                return false;
            }
        };

        HttpClientConfiguration httpClientConfiguration = clientConfiguration.getHttpClientConfiguration();
        HttpClientBuilder httpClientBuilder = new HttpClientBuilder(environment)
            .using(httpClientConfiguration)
            .using(nullRetry);  // Apache's retry mechanism does not allow changing hosts. Do retries manually
        httpClientBuilder = updateHttpClientBuilder(configuration, environment, httpClientBuilder);
        HttpClient httpClient = httpClientBuilder.build(clientName);

        HttpClient client = new WrappedHttpClient(httpClient, SoaBundle.getFeatures(environment).getDiscovery(), retryComponents);
        SoaBundle.getFeatures(environment).putNamed(client, HttpClient.class, clientName);
        return client;
    }
}
