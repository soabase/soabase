package io.soabase.core;

import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.discovery.HealthCheckIntegration;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryHealth;
import io.soabase.core.rest.DiscoveryApis;
import io.soabase.core.rest.DynamicAttributeApis;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletContainer;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoaBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    private final ConfigurationAccessor<T, SoaConfiguration> configurationAccessor;

    public SoaBundle(ConfigurationAccessor<T, SoaConfiguration> configurationAccessor)
    {
        this.configurationAccessor = new CheckedConfigurationAccessor<>(configurationAccessor);
    }

    @Override
    public void run(final T configuration, Environment environment) throws Exception
    {
        final SoaConfiguration soaConfiguration = configurationAccessor.accessConfiguration(configuration);

        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(soaConfiguration).to(SoaFeatures.class);
            }
        };

        checkCorsFilter(soaConfiguration, environment);
        initJerseyAdmin(soaConfiguration, environment, binder);

        updateInstanceName(soaConfiguration);
        List<String> scopes = Lists.newArrayList();
        scopes.add(soaConfiguration.getInstanceName());
        scopes.addAll(soaConfiguration.getScopes());

        int mainPort = getMainPort(configuration);

        SoaDiscovery discovery = checkManaged(environment, soaConfiguration.getDiscoveryFactory().build(mainPort, soaConfiguration, environment));
        SoaDynamicAttributes attributes = checkManaged(environment, soaConfiguration.getAttributesFactory().build(soaConfiguration, environment, scopes));
        soaConfiguration.setDiscovery(discovery);
        soaConfiguration.setAttributes(attributes);

        startDiscoveryHealth(discovery, soaConfiguration, environment);

        environment.jersey().register(binder);

        Managed managed = new Managed()
        {
            @Override
            public void start() throws Exception
            {
                soaConfiguration.lock();
            }

            @Override
            public void stop() throws Exception
            {
                // NOP
            }
        };
        environment.lifecycle().manage(managed);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // NOP
    }

    private int getMainPort(T configuration)
    {
        if ( SoaMainPortAccessor.class.isAssignableFrom(configuration.getClass()) )
        {
            return ((SoaMainPortAccessor)configuration).getMainPort(configuration);
        }

        ServerFactory serverFactory = configuration.getServerFactory();
        if ( SoaMainPortAccessor.class.isAssignableFrom(serverFactory.getClass()) )
        {
            return ((SoaMainPortAccessor)serverFactory).getMainPort(configuration);
        }

        if ( DefaultServerFactory.class.isAssignableFrom(serverFactory.getClass()) )
        {
            List<ConnectorFactory> applicationConnectors = ((DefaultServerFactory)serverFactory).getApplicationConnectors();
            if ( applicationConnectors.size() > 0 )
            {
                ConnectorFactory connectorFactory = applicationConnectors.get(0);
                if ( HttpConnectorFactory.class.isAssignableFrom(connectorFactory.getClass()) )
                {
                    return ((HttpConnectorFactory)connectorFactory).getPort();
                }
            }
        }

        if ( SimpleServerFactory.class.isAssignableFrom(serverFactory.getClass()) )
        {
            ConnectorFactory connectorFactory = ((SimpleServerFactory)serverFactory).getConnector();
            if ( HttpConnectorFactory.class.isAssignableFrom(connectorFactory.getClass()) )
            {
                return ((HttpConnectorFactory)connectorFactory).getPort();
            }
        }

        // TODO logging
        throw new RuntimeException("Cannot determine the main server port");
    }

    private void startDiscoveryHealth(SoaDiscovery discovery, SoaConfiguration soaConfiguration, Environment environment)
    {
        SoaDiscoveryHealth discoveryHealth = checkManaged(environment, soaConfiguration.getDiscoveryHealthFactory().build(soaConfiguration, environment));
        ScheduledExecutorService service = environment.lifecycle().scheduledExecutorService("DiscoveryHealthChecker-%d").build();
        service.scheduleAtFixedRate(new HealthCheckIntegration(environment.healthChecks(), discovery, discoveryHealth), soaConfiguration.getDiscoveryHealthCheckPeriodMs(), soaConfiguration.getDiscoveryHealthCheckPeriodMs(), TimeUnit.MILLISECONDS);
    }

    private void checkCorsFilter(SoaConfiguration configuration, Environment environment)
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

    private void updateInstanceName(SoaConfiguration configuration) throws UnknownHostException
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

    private void initJerseyAdmin(SoaConfiguration configuration, Environment environment, AbstractBinder binder)
    {
        if ( configuration.getAdminJerseyPath() == null )
        {
            return;
        }

        String jerseyRootPath = configuration.getAdminJerseyPath();
        if ( !jerseyRootPath.endsWith("/*") )
        {
            if ( jerseyRootPath.endsWith("/") )
            {
                jerseyRootPath += "*";
            }
            else
            {
                jerseyRootPath += "/*";
            }
        }

        DropwizardResourceConfig jerseyConfig = new DropwizardResourceConfig(environment.metrics());
        JerseyContainerHolder jerseyServletContainer = new JerseyContainerHolder(new ServletContainer(jerseyConfig));
        environment.admin().addServlet("soa-admin-jersey", jerseyServletContainer.getContainer()).addMapping(jerseyRootPath);

        JerseyEnvironment jerseyEnvironment = new JerseyEnvironment(jerseyServletContainer, jerseyConfig);
        configuration.putNamed(jerseyEnvironment, JerseyEnvironment.class, SoaFeatures.ADMIN_NAME);
        jerseyEnvironment.register(DiscoveryApis.class);
        jerseyEnvironment.register(DynamicAttributeApis.class);
        jerseyEnvironment.register(binder);
        jerseyEnvironment.setUrlPattern(jerseyConfig.getUrlPattern());
        jerseyEnvironment.register(new JacksonMessageBodyProvider(environment.getObjectMapper(), environment.getValidator()));
    }
}
