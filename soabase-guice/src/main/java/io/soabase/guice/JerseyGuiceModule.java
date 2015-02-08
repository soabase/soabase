package io.soabase.guice;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.internal.UniqueAnnotations;
import java.util.List;

// heavily copied from Guice Servlet
public class JerseyGuiceModule extends AbstractModule
{
    private final List<FilterDefinition> filterDefinitions = Lists.newArrayList();
    private final List<FilterInstanceBindingEntry> filterInstanceEntries = Lists.newArrayList();
    private final List<ServletDefinition> servletDefinitions = Lists.newArrayList();
    private final List<ServletInstanceBindingEntry> servletInstanceEntries = Lists.newArrayList();

    protected final FilterKeyBindingBuilder filter(String... urlPatterns)
    {
        return new FilterKeyBindingBuilderImpl(this, Lists.newArrayList(urlPatterns));
    }

    protected final ServletKeyBindingBuilder serve(String... urlPatterns)
    {
        return new ServletKeyBindingBuilderImpl(this, Lists.newArrayList(urlPatterns));
    }

    @Override
    protected final void configure()
    {
        configureServlets();

        for ( FilterInstanceBindingEntry entry : filterInstanceEntries )
        {
            bind(entry.key).toInstance(entry.filter);
        }
        for ( ServletInstanceBindingEntry entry : servletInstanceEntries )
        {
            bind(entry.key).toInstance(entry.servlet);
        }

        for ( FilterDefinition filterDefinition : filterDefinitions )
        {
            bind(FilterDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(filterDefinition);
        }
        for ( ServletDefinition servletDefinition : servletDefinitions )
        {
            bind(ServletDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(servletDefinition);
        }
    }

    /**
     * <h3>Servlet Mapping EDSL</h3>
     * <p/>
     * <p> Part of the EDSL builder language for configuring servlets
     * and filters with guice-servlet. Think of this as an in-code replacement for web.xml.
     * Filters and servlets are configured here using simple java method calls. Here is a typical
     * example of registering a filter when creating your Guice injector:
     * <p/>
     * <pre>
     *   Guice.createInjector(..., new ServletModule() {
     *
     *     {@literal @}Override
     *     protected void configureServlets() {
     *       <b>serve("*.html").with(MyServlet.class)</b>
     *     }
     *   }
     * </pre>
     * <p/>
     * This registers a servlet (subclass of {@code HttpServlet}) called {@code MyServlet} to service
     * any web pages ending in {@code .html}. You can also use a path-style syntax to register
     * servlets:
     * <p/>
     * <pre>
     *       <b>serve("/my/*").with(MyServlet.class)</b>
     * </pre>
     * <p/>
     * Every servlet (or filter) is required to be a singleton. If you cannot annotate the class
     * directly, you should add a separate {@code bind(..).in(Singleton.class)} rule elsewhere in
     * your module. Mapping a servlet that is bound under any other scope is an error.
     * <p/>
     * <p/>
     * <h4>Dispatch Order</h4>
     * You are free to register as many servlets and filters as you like this way. They will
     * be compared and dispatched in the order in which the filter methods are called:
     * <p/>
     * <pre>
     *
     *   Guice.createInjector(..., new ServletModule() {
     *
     *     {@literal @}Override
     *     protected void configureServlets() {
     *       filter("/*").through(MyFilter.class);
     *       filter("*.css").through(MyCssFilter.class);
     *       filter("*.jpg").through(new MyJpgFilter());
     *       // etc..
     *
     *       serve("*.html").with(MyServlet.class);
     *       serve("/my/*").with(MyServlet.class);
     *       serve("*.jpg").with(new MyServlet());
     *       // etc..
     *      }
     *    }
     * </pre>
     * This will traverse down the list of rules in lexical order. For example, a url
     * "{@code /my/file.js}" (after it runs through the matching filters) will first
     * be compared against the servlet mapping:
     * <p/>
     * <pre>
     *       serve("*.html").with(MyServlet.class);
     * </pre>
     * And failing that, it will descend to the next servlet mapping:
     * <p/>
     * <pre>
     *       serve("/my/*").with(MyServlet.class);
     * </pre>
     * <p/>
     * Since this rule matches, Guice Servlet will dispatch to {@code MyServlet}. These
     * two mapping rules can also be written in more compact form using varargs syntax:
     * <p/>
     * <pre>
     *       serve(<b>"*.html", "/my/*"</b>).with(MyServlet.class);
     * </pre>
     * <p/>
     * This way you can map several URI patterns to the same servlet. A similar syntax is
     * also available for filter mappings.
     * <p/>
     * <p/>
     * <h4>Regular Expressions</h4>
     * You can also map servlets (or filters) to URIs using regular expressions:
     * <pre>
     *    <b>serveRegex("(.)*ajax(.)*").with(MyAjaxServlet.class)</b>
     * </pre>
     * <p/>
     * This will map any URI containing the text "ajax" in it to {@code MyAjaxServlet}. Such as:
     * <ul>
     * <li>http://www.google.com/ajax.html</li>
     * <li>http://www.google.com/content/ajax/index</li>
     * <li>http://www.google.com/it/is_totally_ajaxian</li>
     * </ul>
     * <p/>
     * <p/>
     * <h3>Initialization Parameters</h3>
     * <p/>
     * Servlets (and filters) allow you to pass in init params
     * using the {@code <init-param>} tag in web.xml. You can similarly pass in parameters to
     * Servlets and filters registered in Guice-servlet using a {@link java.util.Map} of parameter
     * name/value pairs. For example, to initialize {@code MyServlet} with two parameters
     * ({@code name="Dhanji", site="google.com"}) you could write:
     * <p/>
     * <pre>
     *  Map&lt;String, String&gt; params = new HashMap&lt;String, String&gt;();
     *  params.put("name", "Dhanji");
     *  params.put("site", "google.com");
     *
     *  ...
     *      serve("/*").with(MyServlet.class, <b>params</b>)
     * </pre>
     * <p/>
     * <p/>
     * <h3>Binding Keys</h3>
     * <p/>
     * You can also bind keys rather than classes. This lets you hide
     * implementations with package-local visbility and expose them using
     * only a Guice module and an annotation:
     * <p/>
     * <pre>
     *  ...
     *      filter("/*").through(<b>Key.get(Filter.class, Fave.class)</b>);
     * </pre>
     * <p/>
     * Where {@code Filter.class} refers to the Servlet API interface and {@code Fave.class} is a
     * custom binding annotation. Elsewhere (in one of your own modules) you can bind this
     * filter's implementation:
     * <p/>
     * <pre>
     *   bind(Filter.class)<b>.annotatedWith(Fave.class)</b>.to(MyFilterImpl.class);
     * </pre>
     * <p/>
     * See {@link com.google.inject.Binder} for more information on binding syntax.
     * <p/>
     * <p/>
     * <h3>Multiple Modules</h3>
     * <p/>
     * It is sometimes useful to capture servlet and filter mappings from multiple different
     * modules. This is essential if you want to package and offer drop-in Guice plugins that
     * provide servlet functionality.
     * <p/>
     * <p/>
     * Guice Servlet allows you to register several instances of {@code ServletModule} to your
     * injector. The order in which these modules are installed determines the dispatch order
     * of filters and the precedence order of servlets. For example, if you had two servlet modules,
     * {@code RpcModule} and {@code WebServiceModule} and they each contained a filter that mapped
     * to the same URI pattern, {@code "/*"}:
     * <p/>
     * <p/>
     * In {@code RpcModule}:
     * <pre>
     *     filter("/*").through(RpcFilter.class);
     * </pre>
     * <p/>
     * In {@code WebServiceModule}:
     * <pre>
     *     filter("/*").through(WebServiceFilter.class);
     * </pre>
     * <p/>
     * Then the order in which these filters are dispatched is determined by the order in which
     * the modules are installed:
     * <p/>
     * <pre>
     *   <b>install(new WebServiceModule());</b>
     *   install(new RpcModule());
     * </pre>
     * <p/>
     * In the case shown above {@code WebServiceFilter} will run first.
     *
     * @since 2.0
     */
    protected void configureServlets()
    {
    }

    void add(FilterDefinition filterDefinition)
    {
        filterDefinitions.add(filterDefinition);
    }

    void add(FilterInstanceBindingEntry bindingEntry)
    {
        filterInstanceEntries.add(bindingEntry);
    }

    void add(ServletDefinition servletDefinition)
    {
        servletDefinitions.add(servletDefinition);
    }

    void add(ServletInstanceBindingEntry bindingEntry)
    {
        servletInstanceEntries.add(bindingEntry);
    }
}
