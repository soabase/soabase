package io.soabase.admin.components;

import com.google.common.base.Preconditions;

public class AssetsPath
{
    private final String resourcePath;
    private final String uriPath;

    public AssetsPath(String resourcePath, String uriPath)
    {
        resourcePath = Preconditions.checkNotNull(resourcePath, "resourcePath cannot be null");
        uriPath = Preconditions.checkNotNull(uriPath, "uriPath cannot be null");

        Preconditions.checkArgument(resourcePath.startsWith("/"), "%s is not an absolute path", resourcePath);
        Preconditions.checkArgument(!"/".equals(resourcePath), "%s is the classpath root", resourcePath);
        this.resourcePath = resourcePath.endsWith("/") ? resourcePath : (resourcePath + '/');
        this.uriPath = (uriPath.endsWith("/") ? uriPath : (uriPath + '/'));
    }

    public String getResourcePath()
    {
        return resourcePath;
    }

    public String getUriPath()
    {
        return uriPath;
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

        AssetsPath that = (AssetsPath)o;

        if ( !resourcePath.equals(that.resourcePath) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !uriPath.equals(that.uriPath) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = resourcePath.hashCode();
        result = 31 * result + uriPath.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "AssetsPath{" +
            "resourcePath='" + resourcePath + '\'' +
            ", uriPath='" + uriPath + '\'' +
            '}';
    }
}
