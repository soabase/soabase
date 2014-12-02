package io.soabase.core.features.attributes;

import com.google.common.base.Preconditions;

public class AttributeKey
{
    private final String key;
    private final String scope;

    public AttributeKey(String key, String scope)
    {
        this.key = Preconditions.checkNotNull(key, "key cannot be null");
        this.scope = Preconditions.checkNotNull(scope, "scope cannot be null");
    }

    public String getKey()
    {
        return key;
    }

    public String getScope()
    {
        return scope;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        AttributeKey that = (AttributeKey)o;

        if ( !key.equals(that.key) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !scope.equals(that.scope) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "AttributeKey{" +
            "key='" + key + '\'' +
            ", scope='" + scope + '\'' +
            '}';
    }
}
