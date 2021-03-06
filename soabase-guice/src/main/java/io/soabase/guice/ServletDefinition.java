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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.Map;

// heavily copied from Guice Servlet
class ServletDefinition
{
    private final List<String> patterns;
    private final Key<? extends HttpServlet> servletKey;
    private final Map<String, String> initParams;
    private final HttpServlet servletInstance;

    ServletDefinition(List<String> patterns, Key<? extends HttpServlet> servletKey, Map<String, String> initParams, HttpServlet servletInstance)
    {
        this.patterns = ImmutableList.copyOf(patterns);
        this.servletKey = servletKey;
        this.initParams = ImmutableMap.copyOf(Maps.newHashMap(initParams));
        this.servletInstance = servletInstance;
    }

    String[] getPatterns()
    {
        return patterns.toArray(new String[patterns.size()]);
    }

    Key<? extends HttpServlet> getServletKey()
    {
        return servletKey;
    }

    Map<String, String> getInitParams()
    {
        return initParams;
    }

    HttpServlet getServletInstance()
    {
        return servletInstance;
    }

    @Override
    public String toString()
    {
        return "ServletDefinition{" +
            "patterns=" + patterns +
            ", servletKey=" + servletKey +
            ", initParams=" + initParams +
            ", servletInstance=" + servletInstance +
            '}';
    }
}
