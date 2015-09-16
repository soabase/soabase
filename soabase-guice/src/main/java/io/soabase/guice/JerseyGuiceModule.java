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

import com.google.inject.Provides;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ExtendedResourceContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * <p>
 *     Provides much of the functionality of the Guice ServletModule (https://github.com/google/guice/wiki/ServletModule).
 *     All registrations are forwarded to the appropriate Dropwizard/Jersey/Jetty methods.
 * </p>
 *
 * <p>
 *     heavily copied from Guice Servlet
 * </p>
 */
public class JerseyGuiceModule extends JerseyMultiGuiceModule
{
    private final InternalCommonConfig commonConfig = new InternalCommonConfig();

    public JerseyGuiceModule()
    {
        super(new InternalFilter());
    }

    public Configurable<?> configurable()
    {
        return commonConfig;
    }

    @Override
    void internalConfigure()
    {
        bind(InternalFilter.class).toInstance(getFilter());
        bind(InternalCommonConfig.class).toInstance(commonConfig);
        filter("/*").through(getFilter());

        bindScope(RequestScoped.class, ServletScopes.REQUEST);
        bindScope(SessionScoped.class, ServletScopes.SESSION);
    }

    @Provides
    @RequestScoped
    public HttpServletRequest provideHttpServletRequest()
    {
        return getFilter().getServletRequest();
    }

    @Provides
    @RequestScoped
    public HttpServletResponse provideHttpServletResponse()
    {
        return getFilter().getServletResponse();
    }

    @Provides
    @RequestScoped
    public ServletContext provideServletContext()
    {
        HttpServletRequest request = getFilter().getServletRequest();
        return (request != null) ? request.getServletContext() : null;
    }

    @Provides
    @RequestScoped
    public HttpSession provideHttpSession()
    {
        HttpServletRequest request = getFilter().getServletRequest();
        return (request != null) ? request.getSession() : null;
    }

    @Provides
    @RequestParameters
    @RequestScoped
    public Map<String, String[]> provideParameterMap()
    {
        HttpServletRequest request = getFilter().getServletRequest();
        return (request != null) ? request.getParameterMap() : null;
    }

    @Provides
    @RequestScoped
    public ContainerRequestContext providesContainerRequestContext()
    {
        return getFilter().getContainerRequestContext();
    }

    @Provides
    @RequestScoped
    public ExtendedResourceContext providesExtendedResourceContext()
    {
        return getFilter().getResourceContext();
    }

    @Provides
    @RequestScoped
    public ResourceContext providesResourceContext()
    {
        return getFilter().getResourceContext();
    }

    @Provides
    @RequestScoped
    public Request providesRequest()
    {
        return getFilter().getContainerRequestContext().getRequest();
    }

    @Provides
    @RequestScoped
    public UriInfo providesUriInfo()
    {
        ContainerRequestContext context = getFilter().getContainerRequestContext();
        return (context != null) ? context.getUriInfo() : null;
    }

    @Provides
    @RequestScoped
    public HttpHeaders providesHttpHeaders()
    {
        ContainerRequestContext context = getFilter().getContainerRequestContext();
        return (context != null) ? (ContainerRequest)context.getRequest() : null;
    }

    @Provides
    @RequestScoped
    public MessageBodyWorkers providesMessageBodyWorkers()
    {
        ContainerRequestContext context = getFilter().getContainerRequestContext();
        return (context != null) ? ((ContainerRequest)context.getRequest()).getWorkers() : null;
    }

    @Provides
    @RequestScoped
    public SecurityContext providesSecurityContext()
    {
        ContainerRequestContext context = getFilter().getContainerRequestContext();
        return (context != null) ? context.getSecurityContext() : null;
    }
}
