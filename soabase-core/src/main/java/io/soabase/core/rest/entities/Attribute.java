package io.soabase.core.rest.entities;

import com.google.common.base.Preconditions;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Attribute implements Comparable<Attribute>
{
    private String key;
    private String scope;
    private String value;
    private boolean isOverride;

    public Attribute()
    {
        this("", "", "", false);
    }

    public Attribute(String key, String scope, String value, boolean isOverride)
    {
        this.isOverride = isOverride;
        this.key = Preconditions.checkNotNull(key, "key cannot be null");
        this.scope = Preconditions.checkNotNull(scope, "scope cannot be null");
        this.value = Preconditions.checkNotNull(value, "value cannot be null");
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

    public boolean isOverride()
    {
        return isOverride;
    }

    public void setOverride(boolean isOverride)
    {
        this.isOverride = isOverride;
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

        Attribute attribute = (Attribute)o;

        if ( isOverride != attribute.isOverride )
        {
            return false;
        }
        if ( !key.equals(attribute.key) )
        {
            return false;
        }
        if ( !scope.equals(attribute.scope) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !value.equals(attribute.value) )
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
        result = 31 * result + value.hashCode();
        result = 31 * result + (isOverride ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(Attribute that)
    {
        if ( that == null )
        {
            return -1;
        }

        if ( this.equals(that) )
        {
            return 0;
        }

        if ( this.isOverride != that.isOverride )
        {
            return this.isOverride ? -1 : 1;
        }

        int diff = this.key.compareTo(that.key);
        if ( diff == 0 )
        {
            diff = this.scope.compareTo(that.scope);
        }
        return diff;
    }

    @Override
    public String toString()
    {
        return "Attribute{" +
            "key='" + key + '\'' +
            ", scope='" + scope + '\'' +
            ", value='" + value + '\'' +
            ", isOverride=" + isOverride +
            '}';
    }
}
