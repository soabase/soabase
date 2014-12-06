package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CuratorConfiguration
{
    @Valid
    @NotNull
    private String connectionString;

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
}
