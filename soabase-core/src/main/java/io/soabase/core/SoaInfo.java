package io.soabase.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class SoaInfo
{
    private final List<String> scopes;
    private final int mainPort;
    private final String serviceName;
    private final String instanceName;
    private final long startTimeMs = System.currentTimeMillis();

    public SoaInfo(List<String> scopes, int mainPort, String serviceName, String instanceName)
    {
        scopes = Preconditions.checkNotNull(scopes, "scopes cannot be null");
        this.scopes = ImmutableList.copyOf(scopes);
        this.mainPort = mainPort;
        this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName cannot be null");
        this.instanceName = Preconditions.checkNotNull(instanceName, "instanceName cannot be null");
    }

    public List<String> getScopes()
    {
        return scopes;
    }

    public int getMainPort()
    {
        return mainPort;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public long getStartTimeMs()
    {
        return startTimeMs;
    }
}
