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
package io.soabase.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.logging.AppenderFactory;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.config.ComposedConfigurationAccessor;
import io.soabase.core.features.attributes.SafeDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaWritableDynamicAttributes;
import io.soabase.core.features.discovery.HealthCheckIntegration;
import io.soabase.core.features.discovery.SafeSoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryHealth;
import io.soabase.core.features.discovery.SoaExtendedDiscovery;
import io.soabase.core.features.logging.LoggingReader;
import io.soabase.core.features.request.SoaClientFilter;
import io.soabase.core.rest.DiscoveryApis;
import io.soabase.core.rest.DynamicAttributeApis;
import io.soabase.core.rest.LoggingApis;
import io.soabase.core.rest.SoaApis;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletContainer;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoaBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    private static final boolean hasAdminKey;
    static
    {
        boolean localHasAdminKey = false;
        try
        {
            // presence of this key allows mutable attributes
            Class.forName("io.soabase.admin.SoaAdminKey");
            localHasAdminKey = true;
        }
        catch ( ClassNotFoundException e )
        {
            // ignore
        }
        hasAdminKey = localHasAdminKey;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
    }

    public static SoaFeatures getFeatures(Environment environment)
    {
        SoaFeatures features = (SoaFeatures)environment.getApplicationContext().getAttribute(SoaFeatures.class.getName());
        if ( features == null )
        {
            features = new SoaFeaturesImpl();   // temp version so that named values can be set
            environment.getApplicationContext().setAttribute(SoaFeatures.class.getName(), features);
        }
        return features;
    }

    public static <T> T access(Configuration configuration, Environment environment, Class<T> clazz)
    {
        return getAccessor(configuration, environment).access(clazz);
    }

    public static ComposedConfigurationAccessor getAccessor(Configuration configuration, Environment environment)
    {
        ComposedConfigurationAccessor accessor = (ComposedConfigurationAccessor)environment.getApplicationContext().getAttribute(ComposedConfigurationAccessor.class.getName());
        if ( accessor == null )
        {
            accessor = new ComposedConfigurationAccessor(configuration);
            environment.getApplicationContext().setAttribute(ComposedConfigurationAccessor.class.getName(), accessor);
        }
        return accessor;
    }

    @Override
    public void run(final T configuration, Environment environment) throws Exception
    {
        final SoaConfiguration soaConfiguration = SoaBundle.access(configuration, environment, SoaConfiguration.class);

        environment.servlets().addFilter("SoaClientFilter", SoaClientFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        updateInstanceName(soaConfiguration);
        List<String> scopes = Lists.newArrayList();
        scopes.add(soaConfiguration.getInstanceName());
        scopes.add(soaConfiguration.getServiceName());
        scopes.addAll(soaConfiguration.getScopes());

        Ports ports = getPorts(configuration);
        final SoaInfo soaInfo = new SoaInfo(scopes, ports.mainPort, ports.adminPort, soaConfiguration.getServiceName(), soaConfiguration.getInstanceName(), soaConfiguration.isRegisterInDiscovery());

        SoaDiscovery discovery = wrapDiscovery(checkManaged(environment, soaConfiguration.getDiscoveryFactory().build(environment, soaInfo)));
        SoaDynamicAttributes attributes = wrapAttributes(checkManaged(environment, soaConfiguration.getAttributesFactory().build(environment, scopes)));
        LoggingReader loggingReader = initLogging(configuration);

        final SoaFeaturesImpl features = new SoaFeaturesImpl(discovery, attributes, soaInfo, loggingReader);
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(features).to(SoaFeatures.class);
            }
        };
        setFeaturesInContext(environment, features);

        checkCorsFilter(soaConfiguration, environment.servlets());
        initJerseyAdmin(soaConfiguration, features, ports, environment, binder);

        startDiscoveryHealth(discovery, soaConfiguration, environment);

        environment.jersey().register(binder);
    }

    private void setFeaturesInContext(Environment environment, SoaFeaturesImpl features)
    {
        SoaFeaturesImpl tempFeatures = (SoaFeaturesImpl)environment.getApplicationContext().getAttribute(SoaFeatures.class.getName());
        if ( tempFeatures != null )
        {
            features.setNamed(tempFeatures);
        }
        environment.getApplicationContext().setAttribute(SoaFeatures.class.getName(), features);
    }

    private SoaDiscovery wrapDiscovery(SoaDiscovery discovery)
    {
        if ( (discovery instanceof SoaExtendedDiscovery) && !hasAdminKey )
        {
            return new SafeSoaDiscovery(discovery);
        }
        return discovery;
    }

    private SoaDynamicAttributes wrapAttributes(SoaDynamicAttributes attributes)
    {
        if ( (attributes instanceof SoaWritableDynamicAttributes) && !hasAdminKey )
        {
            return new SafeDynamicAttributes(attributes);
        }
        return attributes;
    }

    private static class Ports
    {
        final int mainPort;
        final int adminPort;

        Ports(int mainPort, int adminPort)
        {
            this.mainPort = mainPort;
            this.adminPort = adminPort;
        }
    }

    private Ports getPorts(Configuration configuration)
    {
        if ( SoaMainPortAccessor.class.isAssignableFrom(configuration.getClass()) )
        {
            SoaMainPortAccessor accessor = (SoaMainPortAccessor)configuration;
            return new Ports(accessor.getMainPort(configuration), accessor.getAdminPort(configuration));
        }

        ServerFactory serverFactory = configuration.getServerFactory();
        if ( SoaMainPortAccessor.class.isAssignableFrom(serverFactory.getClass()) )
        {
            SoaMainPortAccessor accessor = (SoaMainPortAccessor)serverFactory;
            return new Ports(accessor.getMainPort(configuration), accessor.getAdminPort(configuration));
        }

        int mainPort = 0;
        int adminPort = 0;
        if ( DefaultServerFactory.class.isAssignableFrom(serverFactory.getClass()) )
        {
            mainPort = portFromConnectorFactories(((DefaultServerFactory)serverFactory).getApplicationConnectors());
            adminPort = portFromConnectorFactories(((DefaultServerFactory)serverFactory).getAdminConnectors());
        }

        if ( SimpleServerFactory.class.isAssignableFrom(serverFactory.getClass()) )
        {
            mainPort = adminPort = portFromConnectorFactory(((SimpleServerFactory)serverFactory).getConnector());
        }

        if ( (mainPort == 0) && (adminPort == 0) )
        {
            throw new RuntimeException("Cannot determine the main server ports");
        }
        return new Ports(mainPort, adminPort);
    }

    private int portFromConnectorFactories(List<ConnectorFactory> applicationConnectors)
    {
        if ( applicationConnectors.size() > 0 )
        {
            return portFromConnectorFactory(applicationConnectors.get(0));
        }
        return 0;
    }

    private int portFromConnectorFactory(ConnectorFactory connectorFactory)
    {
        if ( HttpConnectorFactory.class.isAssignableFrom(connectorFactory.getClass()) )
        {
            HttpConnectorFactory factory = (HttpConnectorFactory)connectorFactory;
            return factory.getPort();
        }
        return 0;
    }

    private void startDiscoveryHealth(SoaDiscovery discovery, SoaConfiguration soaConfiguration, Environment environment)
    {
        SoaDiscoveryHealth discoveryHealth = checkManaged(environment, soaConfiguration.getDiscoveryHealthFactory().build(soaConfiguration, environment));
        ScheduledExecutorService service = environment.lifecycle().scheduledExecutorService("DiscoveryHealthChecker-%d").build();
        service.scheduleAtFixedRate(new HealthCheckIntegration(environment.healthChecks(), discovery, discoveryHealth), soaConfiguration.getDiscoveryHealthCheckPeriodMs(), soaConfiguration.getDiscoveryHealthCheckPeriodMs(), TimeUnit.MILLISECONDS);
    }

    private void checkCorsFilter(SoaConfiguration configuration, ServletEnvironment servlets)
    {
        if ( configuration.isAddCorsFilter() )
        {
            // from http://jitterted.com/tidbits/2014/09/12/cors-for-dropwizard-0-7-x/

            FilterRegistration.Dynamic filter = servlets.addFilter("CORS", CrossOriginFilter.class);
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

    private void initJerseyAdmin(SoaConfiguration configuration, SoaFeaturesImpl features, Ports ports, Environment environment, AbstractBinder binder)
    {
        if ( (configuration.getAdminJerseyPath() == null) || (ports.adminPort == 0) )
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
        features.putNamed(jerseyEnvironment, JerseyEnvironment.class, SoaFeatures.ADMIN_NAME);
        jerseyEnvironment.register(SoaApis.class);
        jerseyEnvironment.register(DiscoveryApis.class);
        jerseyEnvironment.register(DynamicAttributeApis.class);
        jerseyEnvironment.register(LoggingApis.class);
        jerseyEnvironment.register(binder);
        jerseyEnvironment.setUrlPattern(jerseyConfig.getUrlPattern());
        jerseyEnvironment.register(new JacksonMessageBodyProvider(environment.getObjectMapper(), environment.getValidator()));

        checkCorsFilter(configuration, environment.admin());
    }

    private LoggingReader initLogging(Configuration configuration) throws IOException
    {
        Set<File> mainFiles = Sets.newHashSet();
        Set<File> archiveDirectories = Sets.newHashSet();
        for ( AppenderFactory appenderFactory : configuration.getLoggingFactory().getAppenders() )
        {
            if ( appenderFactory instanceof FileAppenderFactory )
            {
                FileAppenderFactory fileAppenderFactory = (FileAppenderFactory)appenderFactory;
                if ( fileAppenderFactory.getCurrentLogFilename() != null )
                {
                    mainFiles.add(new File(fileAppenderFactory.getCurrentLogFilename()).getCanonicalFile());
                }

                if ( fileAppenderFactory.getArchivedLogFilenamePattern() != null )
                {
                    File archive = new File(fileAppenderFactory.getArchivedLogFilenamePattern()).getParentFile().getCanonicalFile();
                    archiveDirectories.add(archive);
                }
            }
        }

        // TODO log if no main files or archives

        return new LoggingReader(mainFiles, archiveDirectories);
    }
}
