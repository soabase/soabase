package io.soabase.zookeeper.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonTypeName("zookeeper")
public class ZooKeeperDiscoveryFactory implements SoaDiscoveryFactory
{
    @Valid
    @NotNull
    private String connectionString;

    @Override
    public SoaDiscovery build(Environment environment)
    {
        return new ZooKeeperDiscovery(this);
    }

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
