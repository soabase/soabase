package io.soabase.admin;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.soabase.core.ConfigurationAccessor;
import io.soabase.core.SoaBundle;
import io.soabase.core.SoaCli;
import io.soabase.core.SoaConfiguration;
import io.soabase.sql.attributes.SqlConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.util.prefs.Preferences;

public class SoaAdminApp extends Application<SoaAdminConfiguration>
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        args = new String[]
        {
            "server",
            "!config.json"
        };
        new SoaAdminApp().run(SoaCli.filter(args));
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
//        bootstrap.addBundle(new SqlBundle<>(soaAccessor, sqlAccessor));
        bootstrap.addBundle(new SoaBundle<>(soaAccessor));
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(SoaAdminConfiguration configuration, Environment environment) throws Exception
    {
        final Preferences preferences = Preferences.userRoot();
        AbstractBinder binder = new AbstractBinder()
        {
            @Override
            protected void configure()
            {
                bind(preferences).to(Preferences.class);
            }
        };
        environment.jersey().register(binder);
        environment.jersey().register(PreferencesResource.class);
    }
}
