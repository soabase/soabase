package io.soabase.admin.auth;

public class AuthSpecHolder
{
    private final AuthSpec authSpec;

    public AuthSpecHolder(AuthSpec authSpec)
    {
        this.authSpec = authSpec;
    }

    public AuthSpec getAuthSpec()
    {
        return authSpec;
    }

    public boolean hasAuth()
    {
        return authSpec != null;
    }
}
