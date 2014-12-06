package io.soabase.client;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;

public class SoaClientBundle<T extends SoaConfiguration & SoaClientConfigurationAccessor> implements ConfiguredBundle<T>
{
    public static final String HOST_SUBSTITUTION_TOKEN = "$";
    private final String clientName;
    private final boolean retry500s;

    public SoaClientBundle(String clientName)
    {
        this(clientName, true);
    }

    public SoaClientBundle(String clientName, boolean retry500s)
    {
        this.clientName = clientName;
        this.retry500s = retry500s;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    public static HttpClient getClient(SoaConfiguration configuration, String clientName)
    {
        return configuration.getNamed(HttpClient.class, toKey(clientName));
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        environment.servlets().addFilter("SoaClientFilter", SoaClientFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        final HttpClient client;
        {
            HttpRequestRetryHandler nullRetry = new HttpRequestRetryHandler()
            {
                @Override
                public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
                {
                    return false;
                }
            };

            HttpClientConfiguration httpClientConfiguration = configuration.getHttpClientConfiguration();
            HttpClient httpClient = new HttpClientBuilder(environment)
                .using(httpClientConfiguration)
                .using(nullRetry)   // Apache's retry mechanism does not allow changing hosts. Do retries manually
                .build(clientName);

            client = new WrappedHttpClient(httpClient, configuration.getDiscovery(), httpClientConfiguration.getRetries(), retry500s);
            configuration.putNamed(client, toKey(clientName));
        }

        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(client).named(clientName).to(HttpClient.class);
            }
        };
        environment.jersey().register(binder);
    }

    private static String toKey(String clientName)
    {
        return SoaClientBundle.class.getName() + "." + clientName;
    }
}
