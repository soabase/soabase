package io.soabase.core.features.discovery;

import com.google.common.base.Preconditions;

public class SoaDiscoveryInstance
{
    private final String host;
    private final int port;
    private final boolean forceSsl;

    public SoaDiscoveryInstance(String host, int port, boolean forceSsl)
    {
        this.host = Preconditions.checkNotNull(host, "host cannot be null");
        this.port = port;
        this.forceSsl = forceSsl;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public boolean isForceSsl()
    {
        return forceSsl;
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

        SoaDiscoveryInstance that = (SoaDiscoveryInstance)o;

        if ( forceSsl != that.forceSsl )
        {
            return false;
        }
        if ( port != that.port )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !host.equals(that.host) )
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
        return "SoaDiscoveryInstance{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", forceSsl=" + forceSsl +
            '}';
    }
}
