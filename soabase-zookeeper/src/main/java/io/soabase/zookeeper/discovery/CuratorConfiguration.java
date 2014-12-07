package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.core.SoaFeatures;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CuratorConfiguration
{
    @Valid
    @NotNull
    private String connectionString;

    @Valid
    @NotNull
    private String curatorName = SoaFeatures.DEFAULT_NAME;

    @JsonProperty("connectionString")
    public String getConnectionString()
    {
        return connectionString;
    }

    @JsonProperty("connectionString")
    public void setConnectionString(String connectionString)
    {
        this.connectionString = connectionString;
    }


    @JsonProperty("name")
    public String getCuratorName()
    {
        return curatorName;
    }

    @JsonProperty("name")
    public void setCuratorName(String curatorName)
    {
        this.curatorName = curatorName;
    }
}
