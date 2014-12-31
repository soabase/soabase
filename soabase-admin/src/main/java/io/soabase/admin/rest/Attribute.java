package io.soabase.admin.rest;

public class Attribute
{
    private String key;
    private String scope;
    private String value;

    public Attribute()
    {
        this("", "", "");
    }

    public Attribute(String key, String scope, String value)
    {
        this.key = key;
        this.scope = scope;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
