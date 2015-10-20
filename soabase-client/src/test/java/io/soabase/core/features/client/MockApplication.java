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
