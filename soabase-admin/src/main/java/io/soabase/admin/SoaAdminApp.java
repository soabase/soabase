package io.soabase.admin;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.admin.components.ComponentManager;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.rest.PreferencesResource;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaCli;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaFeatures;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.util.prefs.Preferences;

public class SoaAdminApp extends Application<SoaAdminConfiguration>
{
    private final SoaAdminOptions options;

    public SoaAdminApp(SoaAdminOptions options)
    {
        this.options = options;
    }

    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        SoaAdminOptions options = SoaAdminOptions.get(args);
        if ( options == null )
        {
            return;
        }

        System.setProperty("dw.curator.connectionString", "tbd");

        String[] internalArgs = new String[]
        {
            "server",
            "!config.json"
        };
        new SoaAdminApp(options).run(SoaCli.filter(internalArgs));
    }

    @Override
    public void initialize(Bootstrap<SoaAdminConfiguration> bootstrap)
    {
        ConfigurationAccessor<SoaAdminConfiguration, SoaConfiguration> soaAccessor = new ConfigurationAccessor<SoaAdminConfiguration, SoaConfiguration>()
        {
            @Override
            public SoaConfiguration accessConfiguration(SoaAdminConfiguration configuration)
            {
                return configuration.getSoaConfiguration();
            }
        };
        ConfigurationAccessor<SoaAdminConfiguration, SqlConfiguration> sqlAccessor = new ConfigurationAccessor<SoaAdminConfiguration, SqlConfiguration>()
        {
            @Override
            public SqlConfiguration accessConfiguration(SoaAdminConfiguration configuration)
            {
                return configuration.getSqlConfiguration();
            }
        };
        ConfigurationAccessor<SoaAdminConfiguration, CuratorConfiguration> curatorAccessor = new ConfigurationAccessor<SoaAdminConfiguration, CuratorConfiguration>()
        {
            @Override
            public CuratorConfiguration accessConfiguration(SoaAdminConfiguration configuration)
            {
                return configuration.getCuratorConfiguration();
            }
        };
//        bootstrap.addBundle(new SqlBundle<>(soaAccessor, sqlAccessor));
        bootstrap.addBundle(new SoaBundle<>(soaAccessor));
//        bootstrap.addBundle(new CuratorBundle<>(soaAccessor, curatorAccessor));
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));
    }

    @Override
    public void run(SoaAdminConfiguration configuration, Environment environment) throws Exception
    {
        final ComponentManager componentManager = new ComponentManager(options.appName, options.company, options.footerMessage);
        final Preferences preferences = Preferences.userRoot();
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(preferences).to(Preferences.class);
                bind(componentManager).to(ComponentManager.class);
            }
        };
        configuration.getSoaConfiguration().putNamed(componentManager, ComponentManager.class, SoaFeatures.DEFAULT_NAME);
        configuration.getSoaConfiguration().putNamed(preferences, Preferences.class, SoaFeatures.DEFAULT_NAME);

        componentManager.addTab(new TabComponent("", "Instances", "assets/main.html"));
        componentManager.addTab(new TabComponent("soa-attributes", "Attributes", "assets/attributes.html"));

        environment.servlets().addServlet("index", new IndexServlet(componentManager)).addMapping("/index.html", "/");

        environment.jersey().register(binder);
        environment.jersey().register(PreferencesResource.class);
    }
}
