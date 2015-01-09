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
package io.soabase.admin.auth;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AuthFilter implements Filter
{
    private final AuthSpec authSpec;

    public AuthFilter(AuthSpec authSpec)
    {
        this.authSpec = authSpec;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // NOP
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    @Override
    public void destroy()
    {
        // NOP
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try
        {
            if ( !authSpec.getAuthMethod().requestIsAuthorized(request).isAuthorized() )
            {
                URI uri;
                try
                {
                    uri = new URI(request.getRequestURI());
                }
                catch ( URISyntaxException e )
                {
                    throw new ServletException(e);
                }

                UriBuilder uriBuilder = UriBuilder.fromUri(uri);
                if ( authSpec.getSignInSslPort() != 0 )
                {
                    uriBuilder = UriBuilder.fromUri(uri).scheme("https").port(authSpec.getSignInSslPort());
                }
                uri = uriBuilder.path("/signin").build();
                response.sendRedirect(uri.toString());
            }
            else
            {
                chain.doFilter(request, response);
            }
        }
        catch ( Exception e )
        {
            throw new ServletException(e);
        }
    }
}
