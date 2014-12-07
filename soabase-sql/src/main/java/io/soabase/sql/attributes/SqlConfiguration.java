package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SqlConfiguration
{
    @Valid
    @NotNull
    private String mybatisConfigUrl;

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
}
