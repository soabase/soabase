package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonTypeName("sql")
public class SqlDynamicAttributesFactory implements SoaDynamicAttributesFactory
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

    @Override
    public SoaDynamicAttributes build(Environment environment, String groupName, String instanceName)
    {
        return new SqlDynamicAttributes(mybatisConfigUrl, groupName, instanceName);
    }
}
