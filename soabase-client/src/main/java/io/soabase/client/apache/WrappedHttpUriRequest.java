package io.soabase.client.apache;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpParams;
import java.net.URI;

@SuppressWarnings("deprecation")
class WrappedHttpUriRequest implements HttpUriRequest
{
    private final HttpUriRequest request;
    private final URI newUri;

    WrappedHttpUriRequest(HttpUriRequest request, URI newUri)
    {
        this.request = request;
        this.newUri = newUri;
    }

    @Override
    public String getMethod()
    {
        return request.getMethod();
    }

    @Override
    public URI getURI()
    {
        return newUri;
    }

    @Override
    public void abort() throws UnsupportedOperationException
    {
        request.abort();
    }

    @Override
    public boolean isAborted()
    {
        return request.isAborted();
    }

    @Override
    public RequestLine getRequestLine()
    {
        return request.getRequestLine();
    }

    @Override
    public ProtocolVersion getProtocolVersion()
    {
        return request.getProtocolVersion();
    }

    @Override
    public boolean containsHeader(String name)
    {
        return request.containsHeader(name);
    }

    @Override
    public Header[] getHeaders(String name)
    {
        return request.getHeaders(name);
    }

    @Override
    public Header getFirstHeader(String name)
    {
        return request.getFirstHeader(name);
    }

    @Override
    public Header getLastHeader(String name)
    {
        return request.getLastHeader(name);
    }

    @Override
    public Header[] getAllHeaders()
    {
        return request.getAllHeaders();
    }

    @Override
    public void addHeader(Header header)
    {
        request.addHeader(header);
    }

    @Override
    public void addHeader(String name, String value)
    {
        request.addHeader(name, value);
    }

    @Override
    public void setHeader(Header header)
    {
        request.setHeader(header);
    }

    @Override
    public void setHeader(String name, String value)
    {
        request.setHeader(name, value);
    }

    @Override
    public void setHeaders(Header[] headers)
    {
        request.setHeaders(headers);
    }

    @Override
    public void removeHeader(Header header)
    {
        request.removeHeader(header);
    }

    @Override
    public void removeHeaders(String name)
    {
        request.removeHeaders(name);
    }

    @Override
    public HeaderIterator headerIterator()
    {
        return request.headerIterator();
    }

    @Override
    public HeaderIterator headerIterator(String name)
    {
        return request.headerIterator(name);
    }

    @Override
    @Deprecated
    public HttpParams getParams()
    {
        return request.getParams();
    }

    @Override
    @Deprecated
    public void setParams(HttpParams params)
    {
        request.setParams(params);
    }
}
