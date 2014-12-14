package io.soabase.core.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Instance
{
    private String host;
    private int port;
    private boolean forceSsl;

    public Instance()
    {
    }

    public Instance(String host, int port, boolean forceSsl)
    {
        this.host = host;
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
}
