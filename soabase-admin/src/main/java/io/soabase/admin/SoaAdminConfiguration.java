package io.soabase.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.soabase.core.SoaConfiguration;
import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorConfiguration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SoaAdminConfiguration extends Configuration
{
    @Valid
    @NotNull
    private SoaConfiguration soaConfiguration = new SoaConfiguration();

    @Valid
    @NotNull
    private SqlConfiguration sqlConfiguration = new SqlConfiguration();

    @Valid
    @NotNull
    private CuratorConfiguration curatorConfiguration = new CuratorConfiguration();

    @JsonProperty("soa")
    public SoaConfiguration getSoaConfiguration()
    {
        return soaConfiguration;
    }

    @JsonProperty("soa")
    public void setSoaConfiguration(SoaConfiguration soaConfiguration)
    {
        this.soaConfiguration = soaConfiguration;
    }

    @JsonProperty("sql")
    public SqlConfiguration getSqlConfiguration()
    {
        return sqlConfiguration;
    }

    @JsonProperty("sql")
    public void setSqlConfiguration(SqlConfiguration sqlConfiguration)
    {
        this.sqlConfiguration = sqlConfiguration;
    }

    @JsonProperty("curator")
    public CuratorConfiguration getCuratorConfiguration()
    {
        return curatorConfiguration;
    }

    @JsonProperty("curator")
    public void setCuratorConfiguration(CuratorConfiguration curatorConfiguration)
    {
        this.curatorConfiguration = curatorConfiguration;
    }
}
