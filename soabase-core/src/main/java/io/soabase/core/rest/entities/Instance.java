package io.soabase.core.rest.entities;

import com.google.common.base.Preconditions;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Instance
{
    private String host;
    private int port;
    private boolean forceSsl;

    public Instance()
    {
        this("", 0, false);
    }

    public Instance(String host, int port, boolean forceSsl)
    {
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
        this.forceSsl = forceSsl;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public boolean isForceSsl()
    {
        return forceSsl;
    }

    public void setForceSsl(boolean forceSsl)
    {
        this.forceSsl = forceSsl;
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

        Instance instance = (Instance)o;

        if ( forceSsl != instance.forceSsl )
        {
            return false;
        }
        if ( port != instance.port )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !host.equals(instance.host) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (forceSsl ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Instance{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            '}';
    }
}
