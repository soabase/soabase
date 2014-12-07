package io.soabase.client;

import com.google.common.base.Preconditions;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.CheckedConfigurationAccessor;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaConfiguration;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;

public class SoaClientBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    public static final String HOST_SUBSTITUTION_TOKEN = "$";

    private final String clientName;
    private final boolean retry500s;
    private final ConfigurationAccessor<T, SoaConfiguration> soaAccessor;
    private final ConfigurationAccessor<T, HttpClientConfiguration> clientAccessor;

    public SoaClientBundle(ConfigurationAccessor<T, SoaConfiguration> soaAccessor, ConfigurationAccessor<T, HttpClientConfiguration> clientAccessor, String clientName)
    {
        this(soaAccessor, clientAccessor, clientName, true);
    }

    public SoaClientBundle(ConfigurationAccessor<T, SoaConfiguration> soaAccessor, ConfigurationAccessor<T, HttpClientConfiguration> clientAccessor, String clientName, boolean retry500s)
    {
        this.soaAccessor = new CheckedConfigurationAccessor<>(soaAccessor);
        this.clientAccessor = new CheckedConfigurationAccessor<>(clientAccessor);
        this.clientName = Preconditions.checkNotNull(clientName, "clientName cannot be null");
        this.retry500s = retry500s;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
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

            HttpClientConfiguration httpClientConfiguration = clientAccessor.accessConfiguration(configuration);
            HttpClient httpClient = new HttpClientBuilder(environment)
                .using(httpClientConfiguration)
                .using(nullRetry)   // Apache's retry mechanism does not allow changing hosts. Do retries manually
                .build(clientName);

            SoaConfiguration soaConfiguration = soaAccessor.accessConfiguration(configuration);
            client = new WrappedHttpClient(httpClient, soaConfiguration.getDiscovery(), httpClientConfiguration.getRetries(), retry500s);
            soaConfiguration.putNamed(client, clientName);
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
}
