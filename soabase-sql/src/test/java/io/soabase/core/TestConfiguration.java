package io.soabase.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.soabase.sql.attributes.SqlConfiguration;

public class TestConfiguration extends Configuration
{
    private SoaConfiguration soaConfiguration = new SoaConfiguration();
    private SqlConfiguration sqlConfiguration = new SqlConfiguration();

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
}
