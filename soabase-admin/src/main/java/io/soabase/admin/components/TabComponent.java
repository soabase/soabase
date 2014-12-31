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

public class TabComponent
{
    private final String id;
    private final String name;
    private final String contentResourcePath;
    private final List<String> javascriptUriPaths;
    private final List<String> cssUriPaths;
    private final List<AssetsPath> assetsPaths;

    TabComponent(String id)
    {
        this.id = id;
        name = null;
        contentResourcePath = null;
        javascriptUriPaths = null;
        cssUriPaths = null;
        assetsPaths = null;
    }

    TabComponent(String id, String name, String contentResourcePath, List<String> javascriptUriPaths, List<String> cssUriPaths, List<AssetsPath> assetsPaths)
    {
        this.id = Preconditions.checkNotNull(id, "id cannot be null");
        this.name = Preconditions.checkNotNull(name, "name cannot be null");
        this.contentResourcePath = Preconditions.checkNotNull(contentResourcePath, "contentResourcePath cannot be null");
        javascriptUriPaths = Preconditions.checkNotNull(javascriptUriPaths, "javascriptUriPaths cannot be null");
        cssUriPaths = Preconditions.checkNotNull(cssUriPaths, "cssUriPaths cannot be null");
        assetsPaths = Preconditions.checkNotNull(assetsPaths, "assetsPaths cannot be null");
        this.javascriptUriPaths = ImmutableList.copyOf(javascriptUriPaths);
        this.cssUriPaths = ImmutableList.copyOf(cssUriPaths);
        this.assetsPaths = ImmutableList.copyOf(assetsPaths);
    }

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

    public List<String> getJavascriptUriPaths()
    {
        return javascriptUriPaths;
    }

    public List<String> getCssUriPaths()
    {
        return cssUriPaths;
    }

    public List<AssetsPath> getAssetsPaths()
    {
        return assetsPaths;
    }

    // IMPORTANT: must only equal id
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

        TabComponent that = (TabComponent)o;

        //noinspection RedundantIfStatement
        if ( !id.equals(that.id) )
        {
            return false;
        }

        return true;
    }

    // IMPORTANT: must only equal id
    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return "TabComponent{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", contentResourcePath='" + contentResourcePath + '\'' +
            ", javascriptUriPaths=" + javascriptUriPaths +
            ", cssUriPaths=" + cssUriPaths +
            ", assetsPaths=" + assetsPaths +
            '}';
    }
}
