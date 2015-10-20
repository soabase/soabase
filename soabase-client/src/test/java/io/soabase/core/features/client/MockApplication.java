package io.soabase.core.features.client;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.client.ClientBuilder;
import io.soabase.core.SoaBundle;
import io.soabase.core.features.config.FlexibleConfigurationSourceProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.ws.rs.client.Client;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MockApplication extends Application<TestConfiguration>
{
    private final CountDownLatch startedLatch = new CountDownLatch(1);
    private Client client;
    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicBoolean throwInternalError = new AtomicBoolean(true);

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap)
    {
        bootstrap.setConfigurationSourceProvider(new FlexibleConfigurationSourceProvider());
        bootstrap.addBundle(new SoaBundle<TestConfiguration>());
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception
    {
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(throwInternalError).to(AtomicBoolean.class);
                bind(counter).to(AtomicInteger.class);
            }
        };
        environment.jersey().register(binder);
        environment.jersey().register(MockResource.class);

        JerseyClientConfiguration clientConfiguration = new JerseyClientConfiguration();
        clientConfiguration.setMaxConnectionsPerRoute(Integer.MAX_VALUE);
        clientConfiguration.setMaxConnections(Integer.MAX_VALUE);
        client = new ClientBuilder(environment).buildJerseyClient(clientConfiguration, "test");

        startedLatch.countDown();
    }

    public AtomicBoolean getThrowInternalError()
    {
        return throwInternalError;
    }

    public CountDownLatch getStartedLatch()
    {
        return startedLatch;
    }

    public Client getClient()
    {
        return client;
    }

    public AtomicInteger getCounter()
    {
        return counter;
    }
}
