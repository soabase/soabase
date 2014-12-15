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
package io.soabase.client.apache;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.params.HttpParams;
import java.net.URI;

public class WrappedHttpRequest implements HttpRequest
{
    private final HttpRequest implementation;
    private final URI newUri;

    public WrappedHttpRequest(HttpRequest implementation, URI newUri)
    {
        this.implementation = implementation;
        this.newUri = newUri;
    }

    @Override
    public RequestLine getRequestLine()
    {
        return new RequestLine()
        {
            @Override
            public String getMethod()
            {
                return implementation.getRequestLine().getMethod();
            }

            @Override
            public ProtocolVersion getProtocolVersion()
            {
                return implementation.getRequestLine().getProtocolVersion();
            }

            @Override
            public String getUri()
            {
                return newUri.toString();
            }
        };
    }

    @Override
    public ProtocolVersion getProtocolVersion()
    {
        return implementation.getProtocolVersion();
    }

    @Override
    public boolean containsHeader(String name)
    {
        return implementation.containsHeader(name);
    }

    @Override
    public Header[] getHeaders(String name)
    {
        return implementation.getHeaders(name);
    }

    @Override
    public Header getFirstHeader(String name)
    {
        return implementation.getFirstHeader(name);
    }

    @Override
    public Header getLastHeader(String name)
    {
        return implementation.getLastHeader(name);
    }

    @Override
    public Header[] getAllHeaders()
    {
        return implementation.getAllHeaders();
    }

    @Override
    public void addHeader(Header header)
    {
        implementation.addHeader(header);
    }

    @Override
    public void addHeader(String name, String value)
    {
        implementation.addHeader(name, value);
    }

    @Override
    public void setHeader(Header header)
    {
        implementation.setHeader(header);
    }

    @Override
    public void setHeader(String name, String value)
    {
        implementation.setHeader(name, value);
    }

    @Override
    public void setHeaders(Header[] headers)
    {
        implementation.setHeaders(headers);
    }

    @Override
    public void removeHeader(Header header)
    {
        implementation.removeHeader(header);
    }

    @Override
    public void removeHeaders(String name)
    {
        implementation.removeHeaders(name);
    }

    @Override
    public HeaderIterator headerIterator()
    {
        return implementation.headerIterator();
    }

    @Override
    public HeaderIterator headerIterator(String name)
    {
        return implementation.headerIterator(name);
    }

    @Override
    @Deprecated
    public HttpParams getParams()
    {
        return implementation.getParams();
    }

    @Override
    @Deprecated
    public void setParams(HttpParams params)
    {
        implementation.setParams(params);
    }
}
