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
import com.google.common.collect.Lists;
import java.util.List;

public class TabComponent
{
    private final String id;
    private final String name;
    private final List<String> scriptPaths;
    private final List<String> cssPaths;
    private final String contentPath;

    public TabComponent(String id, String name, String contentPath)
    {
        this(id, name, contentPath, Lists.<String>newArrayList(), Lists.<String>newArrayList());
    }

    public TabComponent(String id, String name, String contentPath, List<String> scriptPaths, List<String> cssPaths)
    {
        scriptPaths = Preconditions.checkNotNull(scriptPaths, "scriptPaths cannot be null");
        cssPaths = Preconditions.checkNotNull(cssPaths, "cssPaths cannot be null");

        id = Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkArgument(isValidId(id), "id must be a valid unicode identifier: " + id);

        this.id = id;
        this.contentPath = Preconditions.checkNotNull(contentPath, "contentPath cannot be null");
        this.name = Preconditions.checkNotNull(name, "name cannot be null");
        this.scriptPaths = ImmutableList.copyOf(scriptPaths);
        this.cssPaths = ImmutableList.copyOf(cssPaths);
    }

    public String getContentPath()
    {
        return contentPath;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getScriptPaths()
    {
        return scriptPaths;
    }

    public List<String> getCssPaths()
    {
        return cssPaths;
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
            ", scriptPaths=" + scriptPaths +
            ", cssPaths=" + cssPaths +
            ", contentPath='" + contentPath + '\'' +
            '}';
    }

    private boolean isValidId(String id)
    {
        boolean isFirst = true;
        for ( char c : id.toCharArray() )
        {
            if ( isFirst )
            {
                isFirst = false;
                if ( !Character.isUnicodeIdentifierStart(c) )
                {
                    return false;
                }
            }
            else if ( !Character.isUnicodeIdentifierPart(c) && (c != '-') )
            {
                return false;
            }
        }
        return true;
    }
}
