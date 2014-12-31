package io.soabase.admin.components;

public class DefaultTabs
{
    public static TabComponent newServicesTab()
    {
        return TabComponentBuilder.builder()
            .withId("soa-services")
            .withName("Services")
            .withContentResourcePath("assets/services/services.html")
            .addingJavascriptUriPath("/assets/services/js/services.js")
            .addingCssUriPath("/assets/services/css/services.css")
            .addingAssetsPath("/assets/services/js")
            .addingAssetsPath("/assets/services/css")
            .build();
    }

    public static TabComponent newAttributesTab()
    {
        return TabComponentBuilder.builder()
            .withId("soa-attributes")
            .withName("Attributes")
            .withContentResourcePath("assets/attributes/attributes.html")
            .addingJavascriptUriPath("/assets/attributes/js/attributes.js")
            .addingCssUriPath("/assets/attributes/css/attributes.css")
            .addingAssetsPath("/assets/attributes/js")
            .addingAssetsPath("/assets/attributes/css")
            .build();
    }

    private DefaultTabs()
    {
    }
}
