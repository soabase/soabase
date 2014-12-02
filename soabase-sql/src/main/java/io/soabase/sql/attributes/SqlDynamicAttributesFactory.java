package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@JsonTypeName("sql")
public class SqlDynamicAttributesFactory implements SoaDynamicAttributesFactory
{
    @Valid
    @NotNull
    private String mybatisConfigUrl;

    @Valid
    private int refreshPeriodSeconds = 30;

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

    @JsonProperty("refreshPeriodSeconds")
    public int getRefreshPeriodSeconds()
    {
        return refreshPeriodSeconds;
    }

    @JsonProperty("refreshPeriodSeconds")
    public void setRefreshPeriodSeconds(int refreshPeriodSeconds)
    {
        this.refreshPeriodSeconds = refreshPeriodSeconds;
    }

    @Override
    public SoaDynamicAttributes build(Environment environment, String groupName, String instanceName)
    {
        final SqlDynamicAttributes dynamicAttributes = new SqlDynamicAttributes(mybatisConfigUrl, groupName, instanceName);
        ScheduledExecutorService service = environment.lifecycle().scheduledExecutorService("SoaDynamicAttributes-%d", true).build();
        Runnable command = new Runnable()
        {
            @Override
            public void run()
            {
                dynamicAttributes.update();
            }
        };
        service.scheduleAtFixedRate(command, refreshPeriodSeconds, refreshPeriodSeconds, TimeUnit.SECONDS);
        return dynamicAttributes;
    }
}
