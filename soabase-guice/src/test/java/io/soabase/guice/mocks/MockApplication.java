package io.soabase.guice.mocks;

import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.guice.GuiceBundle;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.inject.Provider;
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
        Provider<Injector> injectorProvider = new Provider<Injector>()
        {
            @Override
            public Injector get()
            {
                return injector;
            }
        };
        bootstrap.addBundle(new GuiceBundle(injectorProvider));
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
