package io.soabase.admin.auth;

public class AuthDetails
{
    private final boolean isAuthorized;
    private final String authName;

    public AuthDetails(boolean isAuthorized, String authName)
    {
        this.isAuthorized = isAuthorized;
        this.authName = authName;
    }

    public boolean isAuthorized()
    {
        return isAuthorized;
    }

    public String getAuthName()
    {
        return authName;
    }
}
