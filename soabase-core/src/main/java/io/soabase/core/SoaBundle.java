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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
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
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.ExecutorBuilder;
import io.soabase.core.features.attributes.DynamicAttributes;
import io.soabase.core.features.client.ClientFilter;
import io.soabase.core.features.config.ComposedConfigurationAccessor;
import io.soabase.core.features.config.InternalFeatureRegistrations;
import io.soabase.core.features.config.SoaConfiguration;
import io.soabase.core.features.discovery.Discovery;
import io.soabase.core.features.discovery.DiscoveryHealth;
import io.soabase.core.features.discovery.ExtendedDiscovery;
import io.soabase.core.features.discovery.HealthCheckIntegration;
import io.soabase.core.features.discovery.SafeDiscovery;
import io.soabase.core.features.discovery.deployment.DeploymentGroupManager;
import io.soabase.core.features.logging.LoggingReader;
import io.soabase.core.rest.DiscoveryApis;
import io.soabase.core.rest.DynamicAttributeApis;
import io.soabase.core.rest.LoggingApis;
import io.soabase.core.rest.SoaApis;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is the main integration point for Soabase. This is a required bundle. In
 * general, you should add this bundle before any other Soabase bundle. However, the
 * Service Discovery and Dynamic Attributes bundles must be added before this one.
 *
 * @param <T> your application's configuration type
 */
public class SoaBundle<T extends Configuration> implements ConfiguredBundle<T>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final HostAndPort defaultHostAndPort = HostAndPort.fromHost("localhost");

    static final boolean hasAdminKey;

    static
    {
        boolean localHasAdminKey = false;
        try
        {
            // presence of this key allows mutable attributes
            Class.forName("io.soabase.admin.details.SoaAdminKey");
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
        bootstrap.addBundle(new DynamicAttributesBundle());
    }

    /**
     * Return the SoaFeatures instance. Note: the instance is also
     * registered in Jersey's dependency injection framework.
     *
     * @param environment Dropwizard environment
     * @return SoaFeatures instance
     */
    public static SoaFeatures getFeatures(Environment environment)
    {
        SoaFeaturesImpl features = (SoaFeaturesImpl)environment.getApplicationContext().getAttribute(SoaFeatures.class.getName());
        if ( features == null )
        {
            features = new SoaFeaturesImpl();   // temp version so that named values can be set
            environment.getApplicationContext().setAttribute(SoaFeatures.class.getName(), features);
        }

        if ( !features.hasDynamicAttributes() )
        {
            DynamicAttributes dynamicAttributes = (DynamicAttributes)environment.getApplicationContext().getAttribute(DynamicAttributes.class.getName());
            features.setDynamicAttributes(dynamicAttributes);
        }

        return features;
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception
    {
        final SoaConfiguration soaConfiguration = ComposedConfigurationAccessor.access(configuration, environment, SoaConfiguration.class);

        DeploymentGroupManager deploymentGroupManager = checkManaged(environment, soaConfiguration.getDeploymentGroupFactory().build(configuration, environment));

        environment.servlets().addFilter("SoaClientFilter", ClientFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        List<String> scopes = ((DynamicAttributesBundle.Scopes)environment.getApplicationContext().getAttribute(DynamicAttributesBundle.Scopes.class.getName())).getScopes();
        Ports ports = getPorts(configuration);
        DynamicAttributes dynamicAttributes = (DynamicAttributes)environment.getApplicationContext().getAttribute(DynamicAttributes.class.getName());

        SoaInfo soaInfo = new SoaInfo(scopes, ports.mainPort, ports.adminPort, soaConfiguration.getServiceName(), soaConfiguration.getInstanceName(), soaConfiguration.isRegisterInDiscovery());

        Discovery discovery = wrapDiscovery(checkManaged(environment, soaConfiguration.getDiscoveryFactory().build(configuration, environment, soaInfo)));

        final SoaFeaturesImpl features = new SoaFeaturesImpl(discovery, dynamicAttributes, soaInfo, new ExecutorBuilder(environment.lifecycle()), deploymentGroupManager);
        final LoggingReader loggingReader = initLogging(configuration);
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(features).to(SoaFeatures.class);
                bind(environment.healthChecks()).to(HealthCheckRegistry.class);
                bind(environment.getObjectMapper()).to(ObjectMapper.class);
                bind(environment.metrics()).to(MetricRegistry.class);
                bind(loggingReader).to(LoggingReader.class);
            }
        };
        setFeaturesInContext(environment, features);

        checkCorsFilter(soaConfiguration, environment.servlets());
        initJerseyAdmin(soaConfiguration, features, ports, environment, binder);

        startDiscoveryHealth(discovery, soaConfiguration, configuration, environment);

        environment.jersey().register(binder);

        addMetrics(environment);
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

    private Discovery wrapDiscovery(Discovery discovery)
    {
        if ( (discovery instanceof ExtendedDiscovery) && !hasAdminKey )
        {
            return new SafeDiscovery(discovery);
        }
        return discovery;
    }

    @VisibleForTesting
    static class Ports
    {
        final HostAndPort mainPort;
        final HostAndPort adminPort;

        Ports(HostAndPort mainPort, HostAndPort adminPort)
        {
            this.mainPort = mainPort;
            this.adminPort = adminPort;
        }
    }

    @VisibleForTesting
    static <T extends Configuration> Ports getPorts(T configuration)
    {
        if ( SoaMainPortAccessor.class.isAssignableFrom(configuration.getClass()) )
        {
            @SuppressWarnings("unchecked")
            SoaMainPortAccessor<T> accessor = (SoaMainPortAccessor<T>)configuration;
            return new Ports(accessor.getMainPort(configuration), accessor.getAdminPort(configuration));
        }

        ServerFactory serverFactory = configuration.getServerFactory();
        if ( SoaMainPortAccessor.class.isAssignableFrom(serverFactory.getClass()) )
        {
            @SuppressWarnings("unchecked")
            SoaMainPortAccessor<T> accessor = (SoaMainPortAccessor<T>)serverFactory;
            return new Ports(accessor.getMainPort(configuration), accessor.getAdminPort(configuration));
        }

        HostAndPort mainPort = defaultHostAndPort;
        HostAndPort adminPort = defaultHostAndPort;
        if ( DefaultServerFactory.class.isAssignableFrom(serverFactory.getClass()) )
        {
            mainPort = portFromConnectorFactories(((DefaultServerFactory)serverFactory).getApplicationConnectors());
            adminPort = portFromConnectorFactories(((DefaultServerFactory)serverFactory).getAdminConnectors());
        }

        if ( SimpleServerFactory.class.isAssignableFrom(serverFactory.getClass()) )
        {
            //noinspection ConstantConditions
            mainPort = adminPort = portFromConnectorFactory(((SimpleServerFactory)serverFactory).getConnector());
        }

        if ( !mainPort.hasPort() && !adminPort.hasPort() )
        {
            throw new RuntimeException("Cannot determine the main server ports");
        }
        return new Ports(mainPort, adminPort);
    }

    private static HostAndPort portFromConnectorFactories(List<ConnectorFactory> applicationConnectors)
    {
        if ( applicationConnectors.size() > 0 )
        {
            return portFromConnectorFactory(applicationConnectors.get(0));
        }
        return defaultHostAndPort;
    }

    private static HostAndPort portFromConnectorFactory(ConnectorFactory connectorFactory)
    {
        if ( (connectorFactory != null) && HttpConnectorFactory.class.isAssignableFrom(connectorFactory.getClass()) )
        {
            HttpConnectorFactory factory = (HttpConnectorFactory)connectorFactory;
            String bindHost = MoreObjects.firstNonNull(factory.getBindHost(), "localhost");
            return HostAndPort.fromParts(bindHost, factory.getPort());
        }
        return defaultHostAndPort;
    }

    private void startDiscoveryHealth(Discovery discovery, SoaConfiguration soaConfiguration, T configuration, Environment environment)
    {
        DiscoveryHealth discoveryHealth = checkManaged(environment, soaConfiguration.getDiscoveryHealthFactory().build(configuration, environment));
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

    static <T> T checkManaged(Environment environment, T obj)
    {
        if ( obj instanceof Managed )
        {
            environment.lifecycle().manage((Managed)obj);
        }
        return obj;
    }

    private void initJerseyAdmin(SoaConfiguration configuration, SoaFeaturesImpl features, Ports ports, final Environment environment, AbstractBinder binder)
    {
        if ( (configuration.getAdminJerseyPath() == null) || !ports.adminPort.hasPort() )
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
        jerseyConfig.setUrlPattern(jerseyRootPath);

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
        jerseyEnvironment.register(new JacksonMessageBodyProvider(environment.getObjectMapper()));

        checkCorsFilter(configuration, environment.admin());
        checkAdminGuiceFeature(environment, jerseyEnvironment);
    }

    private void checkAdminGuiceFeature(final Environment environment, final JerseyEnvironment jerseyEnvironment)
    {
        try
        {
            Feature feature = new Feature()
            {
                @Override
                public boolean configure(FeatureContext context)
                {
                    for ( Object obj : environment.jersey().getResourceConfig().getSingletons() )
                    {
                        if ( obj instanceof InternalFeatureRegistrations )
                        {
                            ((InternalFeatureRegistrations)obj).apply(context);
                        }
                    }
                    return true;
                }
            };
            jerseyEnvironment.register(feature);
        }
        catch ( Exception ignore )
        {
            // ignore - GuiceBundle not added
        }
    }

    private LoggingReader initLogging(Configuration configuration) throws IOException
    {
        Set<File> mainFiles = Sets.newHashSet();
        Set<File> archiveDirectories = Sets.newHashSet();
        LoggingFactory loggingFactory = configuration.getLoggingFactory();
        if ( loggingFactory instanceof DefaultLoggingFactory )
        {
            for ( AppenderFactory appenderFactory : ((DefaultLoggingFactory)loggingFactory).getAppenders() )
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
        }

        if ( (mainFiles.size() == 0) && (archiveDirectories.size() == 0) )
        {
            log.warn("No log files found in config");
        }

        return new LoggingReader(mainFiles, archiveDirectories);
    }

    private void addMetrics(Environment environment)
    {
        Metric metric = new Gauge<Double>()
        {
            private double lastValue = 0.0;

            @Override
            public Double getValue()
            {
                try
                {
                    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                    ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
                    AttributeList list = mbs.getAttributes(name, new String[]{"SystemCpuLoad"});
                    if ( (list != null) && (list.size() > 0) )
                    {
                        // unfortunately, this bean reports bad values occasionally. Filter them out.
                        Object value = list.asList().get(0).getValue();
                        double d = (value instanceof Number) ? ((Number)value).doubleValue() : 0.0;
                        d = ((d > 0.0) && (d < 1.0)) ? d : lastValue;
                        lastValue = d;
                        return d;
                    }
                }
                catch ( Exception ignore )
                {
                    // ignore
                }
                return lastValue;
            }
        };
        environment.metrics().register("system.cpu.load", metric);
    }
}
