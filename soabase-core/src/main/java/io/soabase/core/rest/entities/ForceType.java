package io.soabase.core.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ForceType
{
    private boolean register;

    public ForceType()
    {
    }

    public ForceType(boolean register)
    {
        this.register = register;
    }

    public boolean isRegister()
    {
        return register;
    }

    public void setRegister(boolean register)
    {
        this.register = register;
    }
}
