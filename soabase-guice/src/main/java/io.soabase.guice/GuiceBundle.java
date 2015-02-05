package io.soabase.guice;

import com.google.inject.Injector;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import javax.inject.Provider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class GuiceBundle implements Bundle
{
    private final Provider<Injector> injectorProvider;

    public GuiceBundle(Provider<Injector> injectorProvider)
    {
        this.injectorProvider = injectorProvider;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    @Override
    public void run(Environment environment)
    {
        Feature feature = new Feature()
        {
            @Override
            public boolean configure(FeatureContext context)
            {
                ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator(context);
                GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
                GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
                guiceBridge.bridgeGuiceInjector(injectorProvider.get());
                return true;
            }
        };
        environment.jersey().register(feature);
    }
}
