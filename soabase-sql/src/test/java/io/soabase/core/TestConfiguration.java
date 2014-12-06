package io.soabase.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.sql.attributes.SoaSqlConfiguration;
import io.soabase.sql.attributes.SoaSqlConfigurationAccessor;

public class TestConfiguration extends SoaConfiguration implements SoaSqlConfigurationAccessor
{
    private SoaSqlConfiguration soaSqlConfiguration = new SoaSqlConfiguration();

    @Override
    @JsonProperty("sql")
    public SoaSqlConfiguration getSqlConfiguration()
    {
        return soaSqlConfiguration;
    }

    @JsonProperty("sql")
    public void setSqlConfiguration(SoaSqlConfiguration soaSqlConfiguration)
    {
        this.soaSqlConfiguration = soaSqlConfiguration;
    }
}
