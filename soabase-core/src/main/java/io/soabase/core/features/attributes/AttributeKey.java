package io.soabase.core.features.attributes;

import com.google.common.base.Preconditions;

public class AttributeKey
{
    private final String key;
    private final String group;
    private final String instance;

    public AttributeKey(String key, String group, String instance)
    {
        this.key = Preconditions.checkNotNull(key, "key cannot be null");
        this.group = group;
        this.instance = instance;
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

        if ( group != null ? !group.equals(that.group) : that.group != null )
        {
            return false;
        }
        if ( instance != null ? !instance.equals(that.instance) : that.instance != null )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !key.equals(that.key) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key.hashCode();
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "AttributeKey{" +
            "key='" + key + '\'' +
            ", group='" + group + '\'' +
            ", instance='" + instance + '\'' +
            '}';
    }
}
