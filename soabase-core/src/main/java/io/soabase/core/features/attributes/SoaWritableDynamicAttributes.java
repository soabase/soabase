package io.soabase.core.features.attributes;

public interface SoaWritableDynamicAttributes extends SoaDynamicAttributes
{
    public void put(AttributeKey key, Object value);
}
