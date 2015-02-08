package io.soabase.guice;

import com.google.inject.Key;
import javax.servlet.Filter;
import java.util.Map;

/**
 * See the EDSL examples at {@link JerseyGuiceModule#configureServlets()}
 *
 * copied from Guice Servlet
 */
public interface FilterKeyBindingBuilder
{
    public void through(Class<? extends Filter> filterKey);

    public void through(Key<? extends Filter> filterKey);

    public void through(Filter filter);

    public void through(Class<? extends Filter> filterKey, Map<String, String> initParams);

    public void through(Key<? extends Filter> filterKey, Map<String, String> initParams);

    public void through(Filter filter, Map<String, String> initParams);
}
