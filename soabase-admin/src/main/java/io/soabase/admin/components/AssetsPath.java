/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
