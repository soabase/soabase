package io.soabase.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import javax.servlet.http.HttpServlet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An internal representation of a servlet definition mapped to a particular URI pattern. Also
 * performs the request dispatch to that servlet. How nice and OO =)
 */
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
        this.initParams = Collections.unmodifiableMap(Maps.newHashMap(initParams));
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
}
