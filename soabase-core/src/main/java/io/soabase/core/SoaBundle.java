package io.soabase.core;

import com.google.common.collect.Lists;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.rest.DiscoveryApis;
import io.soabase.core.rest.DynamicAttributeApis;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.List;

public class SoaBundle<T extends SoaConfiguration> implements ConfiguredBundle<T>
{
    @Override
    public void run(final T configuration, Environment environment) throws Exception
    {
        environment.jersey().register(DiscoveryApis.class);
        environment.jersey().register(DynamicAttributeApis.class);

        checkCorsFilter(configuration, environment);

        updateInstanceName(configuration);
        List<String> scopes = Lists.newArrayList();
        scopes.add(configuration.getInstanceName());
        scopes.addAll(configuration.getScopes());

        SoaDiscovery discovery = checkManaged(environment, configuration.getDiscoveryFactory().build(configuration, environment));
        SoaDynamicAttributes attributes = checkManaged(environment, configuration.getAttributesFactory().build(configuration, environment, scopes));
        configuration.setDiscovery(discovery);
        configuration.setAttributes(attributes);

        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(configuration).to(SoaConfiguration.class);
            }
        };
        environment.jersey().register(binder);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    private void checkCorsFilter(T configuration, Environment environment)
    {
        if ( configuration.isAddCorsFilter() )
        {
            // from http://jitterted.com/tidbits/2014/09/12/cors-for-dropwizard-0-7-x/

            FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
            filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
            filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
            filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
            filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
            filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
            filter.setInitParameter("allowCredentials", "true");
        }
    }

    private void updateInstanceName(T configuration) throws UnknownHostException
    {
        if ( configuration.getInstanceName() == null )
        {
            configuration.setInstanceName(InetAddress.getLocalHost().getHostName());
        }
    }

    private static <T> T checkManaged(Environment environment, T obj)
    {
        if ( obj instanceof Managed )
        {
            environment.lifecycle().manage((Managed)obj);
        }
        return obj;
    }
}
