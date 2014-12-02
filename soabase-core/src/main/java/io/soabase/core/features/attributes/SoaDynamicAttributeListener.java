package io.soabase.core.features.attributes;

public interface SoaDynamicAttributeListener
{
    public void attributeChanged(String key, String scope);

    public void attributeAdded(String key, String scope);

    public void attributeRemoved(String key, String scope);
}
