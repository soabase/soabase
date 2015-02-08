package io.soabase.guice.mocks;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MockServlet extends HttpServlet
{
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String responseStr = "hello";
        response.setIntHeader("Content-Length", responseStr.length());
        response.getOutputStream().print(responseStr);
    }
}
