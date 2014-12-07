package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.core.SoaFeatures;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SqlConfiguration
{
    @Valid
    @NotNull
    private String mybatisConfigUrl;

    @Valid
    @NotNull
    private String sessionName = SoaFeatures.DEFAULT_NAME;

    @JsonProperty("mybatisConfigUrl")
    public String getMybatisConfigUrl()
    {
        return mybatisConfigUrl;
    }

    @JsonProperty("mybatisConfigUrl")
    public void setMybatisConfigUrl(String mybatisConfigUrl)
    {
        this.mybatisConfigUrl = mybatisConfigUrl;
    }

    @JsonProperty("name")
    public String getSessionName()
    {
        return sessionName;
    }

    @JsonProperty("name")
    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }
}
