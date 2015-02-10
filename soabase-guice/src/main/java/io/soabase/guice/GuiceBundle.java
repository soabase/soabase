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

package io.soabase.guice;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Provider;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Type;
import java.util.EnumSet;

/**
 * Bundle for adding Guice support to Jersey 2.0 Resources
 */
public class GuiceBundle implements Bundle
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Provider<Injector> injectorProvider;

    /**
     * @param injectorProvider a provider for the Guice injector to use
     */
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
    public void run(final Environment environment)
    {
        Feature feature = new Feature()
        {
            @Override
            public boolean configure(FeatureContext context)
            {
                ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator(context);
                GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
                GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
                Injector injector = injectorProvider.get();
                guiceBridge.bridgeGuiceInjector(injector);
                registerBoundJerseyComponents(injector, context, environment);
                return true;
            }
        };
        environment.jersey().register(feature);
    }

    private void registerBoundJerseyComponents(Injector injector, FeatureContext context, Environment environment)
    {
        // mostly copied from GuiceComponentProviderFactory#register(ResourceConfig, Injector)
        while ( injector != null )
        {
            for ( Key<?> key : injector.getBindings().keySet() )
            {
                Type type = key.getTypeLiteral().getType();
                if ( type instanceof Class )
                {
                    Class<?> c = (Class)type;
                    if ( isProviderClass(c) )
                    {
                        log.info("Registering {0} as a provider class", c.getName());
                        context.register(c);
                    }
                    else if ( isRootResourceClass(c) )
                    {
                        log.info("Registering {0} as a root resource class", c.getName());
                        context.register(c);
                    }
                    else if ( FilterDefinition.class.equals(c) )
                    {
                        registerFilter(injector, environment, injector.getBinding(key));
                    }
                    else if ( ServletDefinition.class.equals(c) )
                    {
                        registerServlet(injector, environment, injector.getBinding(key));
                    }
                    else if ( InternalFilter.class.equals(c) )
                    {
                        context.register(injector.getBinding(key).getProvider().get());
                    }
                }
            }
            injector = injector.getParent();
        }
    }

    private void registerServlet(Injector injector, Environment environment, Binding<?> binding)
    {
        ServletDefinition servletDefinition = (ServletDefinition)binding.getProvider().get();
        HttpServlet servletInstance = servletDefinition.getServletInstance();
        if ( servletInstance == null )
        {
            servletInstance = injector.getInstance(servletDefinition.getServletKey());
        }
        ServletRegistration.Dynamic registration = environment.servlets().addServlet(servletDefinition.getServletKey().toString(), servletInstance);
        registration.setInitParameters(servletDefinition.getInitParams());
        registration.addMapping(servletDefinition.getPatterns());
    }

    private void registerFilter(Injector injector, Environment environment, Binding<?> binding)
    {
        FilterDefinition filterDefinition = (FilterDefinition)binding.getProvider().get();
        Filter filterInstance = filterDefinition.getFilterInstance();
        if ( filterInstance == null )
        {
            filterInstance = injector.getInstance(filterDefinition.getFilterKey());
        }
        FilterRegistration.Dynamic registration = environment.servlets().addFilter(filterDefinition.getFilterKey().toString(), filterInstance);
        registration.setInitParameters(filterDefinition.getInitParams());
        registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, filterDefinition.getUriPatterns());
    }

    // copied from Jersey 1.17.1
    private static boolean isProviderClass(Class<?> c)
    {
        return (c != null) && c.isAnnotationPresent(javax.ws.rs.ext.Provider.class);
    }

    // copied from Jersey 1.17.1
    private static boolean isRootResourceClass(Class<?> c)
    {
        if ( c == null )
        {
            return false;
        }
        else if ( c.isAnnotationPresent(Path.class) )
        {
            return true;
        }
        else
        {
            Class[] arr = c.getInterfaces();
            int len = arr.length;

            //noinspection ForLoopReplaceableByForEach
            for ( int i = 0; i < len; ++i )
            {
                Class clazz = arr[i];
                if ( clazz.isAnnotationPresent(Path.class) )
                {
                    return true;
                }
            }

            return false;
        }
    }
}
