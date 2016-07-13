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
package io.soabase.guice.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class ExampleFilter implements Filter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ExampleInjected map;

    @Inject // important! use javax annotations, not Guice annotations
    public ExampleFilter(ExampleInjected map)
    {
        this.map = map;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // NOP
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        log.info("Map size is {}", map.size());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
        // NOP
    }
}
