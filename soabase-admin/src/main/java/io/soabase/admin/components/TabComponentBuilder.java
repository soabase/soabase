package io.soabase.admin.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class TabComponentBuilder
{
    private String id;
    private String name;
    private String contentResourcePath;
    private List<String> javascriptUriPaths = Lists.newArrayList();
    private List<String> cssUriPaths = Lists.newArrayList();
    private List<AssetsPath> assetsPaths = Lists.newArrayList();

    public static TabComponentBuilder builder()
    {
        return new TabComponentBuilder();
    }

    public TabComponentBuilder withId(String id)
    {
        this.id = Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkArgument(isValidId(id), "id must be a valid unicode identifier: " + id);
        return this;
    }

    public TabComponentBuilder withName(String name)
    {
        this.name = Preconditions.checkNotNull(name, "name cannot be null");
        return this;
    }

    public TabComponentBuilder withContentResourcePath(String contentResourcePath)
    {
        this.contentResourcePath = Preconditions.checkNotNull(contentResourcePath, "contentResourcePath cannot be null");
        return this;
    }

    public TabComponentBuilder addingJavascriptUriPath(String javascriptUriPath)
    {
        javascriptUriPath = Preconditions.checkNotNull(javascriptUriPath, "javascriptUriPath cannot be null");
        Preconditions.checkArgument(!javascriptUriPaths.contains(javascriptUriPath), "javascriptUriPath has already been added: " + javascriptUriPath);
        javascriptUriPaths.add(javascriptUriPath);
        return this;
    }

    public TabComponentBuilder addingCssUriPath(String cssUriPath)
    {
        cssUriPath = Preconditions.checkNotNull(cssUriPath, "cssUriPath cannot be null");
        Preconditions.checkArgument(!cssUriPaths.contains(cssUriPath), "cssUriPath has already been added: " + cssUriPath);
        cssUriPaths.add(cssUriPath);
        return this;
    }

    public TabComponentBuilder addingAssetsPath(String resourcePath)
    {
        return addingAssetsPath(resourcePath, resourcePath);
    }

    public TabComponentBuilder addingAssetsPath(String resourcePath, String uriPath)
    {
        AssetsPath assetsPath = new AssetsPath(resourcePath, uriPath);
        Preconditions.checkArgument(!assetsPaths.contains(assetsPath), "assetsPath has already been added: " + assetsPath);
        assetsPaths.add(assetsPath);
        return this;
    }

    public TabComponent build()
    {
        return new TabComponent(id, name, contentResourcePath, javascriptUriPaths, cssUriPaths, assetsPaths);
    }

    private TabComponentBuilder()
    {
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
