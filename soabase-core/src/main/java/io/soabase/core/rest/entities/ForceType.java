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

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ForceType forceType = (ForceType)o;

        //noinspection RedundantIfStatement
        if ( register != forceType.register )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return (register ? 1 : 0);
    }

    @Override
    public String toString()
    {
        return "ForceType{" +
            "register=" + register +
            '}';
    }
}
