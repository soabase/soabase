package io.soabase.guice.mocks;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class MockFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = new HttpServletRequestWrapper((HttpServletRequest)request)
        {
            @Override
            public String getQueryString()
            {
                return "test=success";
            }
        };
        chain.doFilter(httpServletRequest, response);
    }

    @Override
    public void destroy()
    {

    }
}
