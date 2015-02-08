package io.soabase.guice;

import com.google.inject.Key;
import javax.servlet.http.HttpServlet;
import java.util.Map;

public interface ServletKeyBindingBuilder
{
    public void with(Class<? extends HttpServlet> servletKey);

    public void with(Key<? extends HttpServlet> servletKey);

    public void with(HttpServlet servlet);

    public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams);

    public void with(Key<? extends HttpServlet> servletKey, Map<String, String> initParams);

    public void with(HttpServlet servlet, Map<String, String> initParams);
}
