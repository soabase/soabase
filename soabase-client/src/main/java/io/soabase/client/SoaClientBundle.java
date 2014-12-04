package io.soabase.client;

import com.google.common.base.Preconditions;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.features.SoaFeatures;
import org.apache.http.client.HttpClient;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;

public class SoaClientBundle<T extends SoaConfiguration & SoaClientAccessor> implements ConfiguredBundle<T>
{
    private final String clientName;
    private final AtomicReference<HttpClient> client = new AtomicReference<>();

    public SoaClientBundle(String clientName)
    {
        this.clientName = clientName;
    }

    public HttpClient getClient()
    {
        return client.get();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        SoaFeatures features = Preconditions.checkNotNull(configuration.getFeatures(), "features have not been set yet. Check execution order.");

        environment.servlets().addFilter("SoaClientFilter", SoaClientFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        HttpClientConfiguration httpClientConfiguration = configuration.getHttpClientConfiguration();
        HttpClient httpClient = new HttpClientBuilder(environment)
            .using(httpClientConfiguration)
            .using(new SoaRetryHandler(httpClientConfiguration.getRetries()))
            .build(clientName);

        client.set(httpClient);

        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(client.get()).named(clientName).to(HttpClient.class);
            }
        };
        environment.jersey().register(binder);
    }
}
