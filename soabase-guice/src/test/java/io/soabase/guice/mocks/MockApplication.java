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
package io.soabase.guice.mocks;

import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.guice.GuiceBundle;
import io.soabase.guice.StandardInjectorProvider;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.util.concurrent.CountDownLatch;

public class MockApplication extends Application<Configuration>
{
    private final Injector injector;
    private final CountDownLatch startedLatch = new CountDownLatch(1);

    public MockApplication(Injector injector)
    {
        this.injector = injector;
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap)
    {
        bootstrap.addBundle(new GuiceBundle(new StandardInjectorProvider(injector)));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception
    {
        AbstractBinder abstractBinder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(new MockHK2Injected()).to(MockHK2Injected.class);
            }
        };
        environment.jersey().register(abstractBinder);
        environment.jersey().register(MockResource.class);
        LifeCycle.Listener listener = new AbstractLifeCycle.AbstractLifeCycleListener()
        {
            @Override
            public void lifeCycleStarted(LifeCycle event)
            {
                System.out.println("Starting...");
                startedLatch.countDown();
            }
        };
        environment.lifecycle().addLifeCycleListener(listener);
    }

    public CountDownLatch getStartedLatch()
    {
        return startedLatch;
    }
}
