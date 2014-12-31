package io.soabase.admin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.soabase.admin.components.DefaultTabs;
import io.soabase.admin.components.TabComponent;
import io.soabase.admin.details.BundleSpec;
import java.util.List;

public class AdminConsoleAppBuilder<T extends Configuration>
{
    private String appName = "Soabase";
    private String companyName = "";
    private String footerMessage = "- Internal use only - Proprietary and Confidential";
    private String soaConfigFieldName = "soa";
    private final List<TabComponent> tabs = Lists.newArrayList();
    private final List<BundleSpec<T>> bundles = Lists.newArrayList();

    public static <T extends Configuration> AdminConsoleAppBuilder<T> builder()
    {
        return new AdminConsoleAppBuilder<T>();
    }

    public AdminConsoleAppBuilder<T> withSoaConfigFieldName(String soaConfigFieldName)
    {
        this.soaConfigFieldName = soaConfigFieldName;
        return this;
    }

    public AdminConsoleAppBuilder<T> withAppName(String appName)
    {
        this.appName = Preconditions.checkNotNull(appName, "appName cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withCompanyName(String companyName)
    {
        this.companyName = Preconditions.checkNotNull(companyName, "companyName cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> withFooterMessage(String footerMessage)
    {
        this.footerMessage = Preconditions.checkNotNull(footerMessage, "footerMessage cannot be null");
        return this;
    }

    public AdminConsoleAppBuilder<T> addingTabComponent(TabComponent tabComponent)
    {
        tabComponent = Preconditions.checkNotNull(tabComponent, "tabComponent cannot be null");
        tabs.add(tabComponent);
        return this;
    }

    public AdminConsoleAppBuilder<T> addingStandardTabs()
    {
        return addingTabComponent(DefaultTabs.newServicesTab()).addingTabComponent(DefaultTabs.newAttributesTab());
    }

    public AdminConsoleAppBuilder<T> addingPreSoaBundle(Bundle bundle)
    {
        bundle = Preconditions.checkNotNull(bundle, "bundle cannot be null");
        bundles.add(new BundleSpec<T>(bundle, BundleSpec.Phase.PRE_SOA));
        return this;
    }

    public AdminConsoleAppBuilder<T> addingPreSoaBundle(ConfiguredBundle<T> bundle)
    {
        bundle = Preconditions.checkNotNull(bundle, "bundle cannot be null");
        bundles.add(new BundleSpec<>(bundle, BundleSpec.Phase.PRE_SOA));
        return this;
    }

    public AdminConsoleAppBuilder<T> addingPostSoaBundle(Bundle bundle)
    {
        bundle = Preconditions.checkNotNull(bundle, "bundle cannot be null");
        bundles.add(new BundleSpec<T>(bundle, BundleSpec.Phase.POST_SOA));
        return this;
    }

    public AdminConsoleAppBuilder<T> addingPostSoaBundle(ConfiguredBundle<T> bundle)
    {
        bundle = Preconditions.checkNotNull(bundle, "bundle cannot be null");
        bundles.add(new BundleSpec<>(bundle, BundleSpec.Phase.POST_SOA));
        return this;
    }

    String getAppName()
    {
        return appName;
    }

    String getCompanyName()
    {
        return companyName;
    }

    String getFooterMessage()
    {
        return footerMessage;
    }

    String getSoaConfigFieldName()
    {
        return soaConfigFieldName;
    }

    List<TabComponent> getTabs()
    {
        return tabs;
    }

    List<BundleSpec<T>> getBundles()
    {
        return bundles;
    }

    private AdminConsoleAppBuilder()
    {
    }
}
