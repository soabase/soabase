package io.soabase.zookeeper.discovery;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class Payload
{
    private int adminPort;
    private Map<String, String> metaData;

    public Payload()
    {
        this(0, Maps.<String, String>newHashMap());
    }

    public Payload(int adminPort, Map<String, String> metaData)
    {
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.adminPort = adminPort;
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    public int getAdminPort()
    {
        return adminPort;
    }

    public void setAdminPort(int adminPort)
    {
        this.adminPort = adminPort;
    }

    public Map<String, String> getMetaData()
    {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData)
    {
        metaData = Preconditions.checkNotNull(metaData, "metaData cannot be null");
        this.metaData = ImmutableMap.copyOf(metaData);
    }
}
