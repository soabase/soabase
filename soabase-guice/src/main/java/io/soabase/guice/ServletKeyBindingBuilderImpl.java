package io.soabase.guice;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ServletKeyBindingBuilderImpl implements ServletKeyBindingBuilder
{
    private final List<String> uriPatterns;
    private final JerseyGuiceModule module;

    ServletKeyBindingBuilderImpl(JerseyGuiceModule module, List<String> uriPatterns)
    {
        this.module = module;
        this.uriPatterns = ImmutableList.copyOf(uriPatterns);
    }

    public void with(Class<? extends HttpServlet> servletKey)
    {
        with(Key.get(servletKey));
    }

    public void with(Key<? extends HttpServlet> servletKey)
    {
        with(servletKey, new HashMap<String, String>());
    }

    public void with(HttpServlet servlet)
    {
        with(servlet, new HashMap<String, String>());
    }

    public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams)
    {
        with(Key.get(servletKey), initParams);
    }

    public void with(Key<? extends HttpServlet> servletKey, Map<String, String> initParams)
    {
        with(servletKey, initParams, null);
    }

    private void with(Key<? extends HttpServlet> servletKey, Map<String, String> initParams, HttpServlet servletInstance)
    {
        module.add(new ServletDefinition(uriPatterns, servletKey, initParams, servletInstance));
    }

    public void with(HttpServlet servlet, Map<String, String> initParams)
    {
        Key<HttpServlet> servletKey = Key.get(HttpServlet.class, UniqueAnnotations.create());
        module.add(new ServletInstanceBindingEntry(servletKey, servlet));
        with(servletKey, initParams, servlet);
    }
}
