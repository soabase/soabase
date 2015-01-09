package io.soabase.admin.rest;

public class AuthStatus
{
    private String name;
    private AuthStatusType type;

    public AuthStatus()
    {
        this(null, AuthStatusType.NOT_LOGGED_IN);
    }

    public AuthStatus(String name, AuthStatusType type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public AuthStatusType getType()
    {
        return type;
    }

    public void setType(AuthStatusType type)
    {
        this.type = type;
    }
}
