package io.soabase.core;

public interface SoaFeatures
{
    public static final String DEFAULT_NAME = "";

    public <T> T getNamed(Class<T> clazz, String name);
}
