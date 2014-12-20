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
    private String appName = "Soabase";

    @Valid
    @NotNull
    private String company = "";

    @Valid
    @NotNull
    private String footerMessage = "- Internal use only - Proprietary and Confidential";

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

    @JsonProperty("appName")
    public String getAppName()
    {
        return appName;
    }

    @JsonProperty("appName")
    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    @JsonProperty("company")
    public String getCompany()
    {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(String company)
    {
        this.company = company;
    }

    @JsonProperty("footerMessage")
    public String getFooterMessage()
    {
        return footerMessage;
    }

    @JsonProperty("footerMessage")
    public void setFooterMessage(String footerMessage)
    {
        this.footerMessage = footerMessage;
    }
}
