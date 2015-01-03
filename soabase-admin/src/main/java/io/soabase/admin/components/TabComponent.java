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
import com.google.common.collect.ImmutableList;
import java.util.List;

public class TabComponent implements ComponentId
{
    private final String id;
    private final String name;
    private final String contentResourcePath;
    private final List<String> javascriptUris;
    private final List<String> cssUris;
    private final List<AssetsPath> assetsPaths;

    TabComponent(String id)
    {
        this.id = id;
        name = null;
        contentResourcePath = null;
        javascriptUris = null;
        cssUris = null;
        assetsPaths = null;
    }

    TabComponent(String id, String name, String contentResourcePath, List<String> javascriptUris, List<String> cssUris, List<AssetsPath> assetsPaths)
    {
        this.id = Preconditions.checkNotNull(id, "id cannot be null");
        this.name = Preconditions.checkNotNull(name, "name cannot be null");
        this.contentResourcePath = Preconditions.checkNotNull(contentResourcePath, "contentResourcePath cannot be null");
        javascriptUris = Preconditions.checkNotNull(javascriptUris, "javascriptUris cannot be null");
        cssUris = Preconditions.checkNotNull(cssUris, "cssUris cannot be null");
        assetsPaths = Preconditions.checkNotNull(assetsPaths, "assetsPaths cannot be null");
        this.javascriptUris = ImmutableList.copyOf(javascriptUris);
        this.cssUris = ImmutableList.copyOf(cssUris);
        this.assetsPaths = ImmutableList.copyOf(assetsPaths);
    }

    @Override
    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getContentResourcePath()
    {
        return contentResourcePath;
    }

    public List<String> getJavascriptUris()
    {
        return javascriptUris;
    }

    public List<String> getCssUris()
    {
        return cssUris;
    }

    public List<AssetsPath> getAssetsPaths()
    {
        return assetsPaths;
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

        TabComponent component = (TabComponent)o;

        if ( !assetsPaths.equals(component.assetsPaths) )
        {
            return false;
        }
        if ( !contentResourcePath.equals(component.contentResourcePath) )
        {
            return false;
        }
        if ( !cssUris.equals(component.cssUris) )
        {
            return false;
        }
        if ( !id.equals(component.id) )
        {
            return false;
        }
        if ( !javascriptUris.equals(component.javascriptUris) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !name.equals(component.name) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + contentResourcePath.hashCode();
        result = 31 * result + javascriptUris.hashCode();
        result = 31 * result + cssUris.hashCode();
        result = 31 * result + assetsPaths.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "TabComponent{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", contentResourcePath='" + contentResourcePath + '\'' +
            ", javascriptUriPaths=" + javascriptUris +
            ", cssUriPaths=" + cssUris +
            ", assetsPaths=" + assetsPaths +
            '}';
    }
}
