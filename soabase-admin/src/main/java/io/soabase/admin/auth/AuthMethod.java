package io.soabase.admin.auth;

import javax.servlet.http.HttpServletRequest;

public interface AuthMethod
{
    public AuthDetails requestIsAuthorized(HttpServletRequest request) throws Exception;

    public boolean login(HttpServletRequest request, String username, String email, String domain, String password) throws Exception;

    public void logOut(HttpServletRequest request) throws Exception;
}
