package io.soabase.client;

import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import io.soabase.client.apache.WrappedHttpClient;
import io.soabase.client.jersey.JerseyRetryConnectorProvider;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.client.DefaultRetryHandler;
import io.soabase.core.features.client.RetryComponents;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.ws.rs.client.Client;
import java.io.IOException;

public class ClientBuilder
{
    private final RetryComponents retryComponents;
    private final Environment environment;

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final boolean DEFAULT_RETRY_500s = true;

    public ClientBuilder(Environment environment)
    {
        this(environment, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_500s);
    }

    public ClientBuilder(Environment environment, int maxRetries)
    {
        this(environment, maxRetries, DEFAULT_RETRY_500s);
    }

    public ClientBuilder(Environment environment, int maxRetries, boolean retry500s)
    {
        this.environment = environment;
        final SoaFeatures features = SoaBundle.getFeatures(environment);
        retryComponents = new RetryComponents(new DefaultRetryHandler(), features.getDiscovery(), maxRetries, retry500s, features.getExecutorBuilder());

        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                for ( String name : features.getNames(Client.class) )
                {
                    bind(features.getNamed(Client.class, name)).named(name).to(Client.class);
                }
                for ( String name : features.getNames(HttpClient.class) )
                {
                    bind(features.getNamed(HttpClient.class, name)).named(name).to(HttpClient.class);
                }
            }
        };
        environment.jersey().register(binder);
    }

    public RetryComponents getRetryComponents()
    {
        return retryComponents;
    }

    public Client buildJerseyClient(JerseyClientConfiguration configuration, String clientName)
    {
        Client client = new JerseyClientBuilder(environment)
            .using(configuration)
            .using(new JerseyRetryConnectorProvider(retryComponents))
            .build(clientName);

        SoaBundle.getFeatures(environment).putNamed(client, Client.class, clientName);

        return client;
    }

    public HttpClient buildHttpClient(HttpClientConfiguration configuration, String clientName)
    {
        HttpRequestRetryHandler nullRetry = new HttpRequestRetryHandler()
        {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
            {
                return false;
            }
        };

        HttpClient httpClient = new HttpClientBuilder(environment)
            .using(configuration)
            .using(nullRetry)  // Apache's retry mechanism does not allow changing hosts. Do retries manually
            .build(clientName);
        HttpClient client = new WrappedHttpClient(httpClient, retryComponents);

        SoaBundle.getFeatures(environment).putNamed(client, HttpClient.class, clientName);

        return client;
    }
}
