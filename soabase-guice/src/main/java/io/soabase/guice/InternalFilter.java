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

import org.glassfish.jersey.server.ExtendedResourceContext;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import java.io.IOException;

@PreMatching
class InternalFilter implements Filter, ContainerRequestFilter
{
    private volatile HttpServletRequest servletRequest;
    private volatile HttpServletResponse servletResponse;
    private volatile ContainerRequestContext containerRequestContext;
    private static final ThreadLocal<InternalFilter> threadLocal = new ThreadLocal<>();

    @Context
    private volatile ExtendedResourceContext resourceContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // NOP
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException
    {
        containerRequestContext = requestContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        servletRequest = (HttpServletRequest)request;
        servletResponse = (HttpServletResponse)response;
        try
        {
            threadLocal.set(this);
            chain.doFilter(request, response);
        }
        finally
        {
            threadLocal.remove();
            servletRequest = null;
            servletResponse = null;
            containerRequestContext = null;
        }
    }

    @Override
    public void destroy()
    {
        // NOP
    }

    static InternalFilter get()
    {
        return threadLocal.get();
    }

    ExtendedResourceContext getResourceContext()
    {
        return resourceContext;
    }

    ContainerRequestContext getContainerRequestContext()
    {
        return containerRequestContext;
    }

    HttpServletRequest getServletRequest()
    {
        return servletRequest;
    }

    HttpServletResponse getServletResponse()
    {
        return servletResponse;
    }
}
