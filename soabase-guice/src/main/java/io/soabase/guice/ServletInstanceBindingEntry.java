package io.soabase.guice;

import com.google.inject.Key;
import javax.servlet.http.HttpServlet;

class ServletInstanceBindingEntry
{
    final Key<HttpServlet> key;
    final HttpServlet servlet;

    ServletInstanceBindingEntry(Key<HttpServlet> key, HttpServlet servlet)
    {
        this.key = key;
        this.servlet = servlet;
    }
}
