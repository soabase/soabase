package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import org.apache.ibatis.session.SqlSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@JsonTypeName("sql")
public class SqlDynamicAttributesFactory implements SoaDynamicAttributesFactory
{
    @Valid
    private int refreshPeriodSeconds = 30;

    @Valid
    @NotNull
    private String sessionName = SoaFeatures.DEFAULT_NAME;

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

    @Override
    public SoaDynamicAttributes build(SoaConfiguration configuration, Environment environment, List<String> scopes)
    {
        SqlSession sqlSession = configuration.getNamedRequired(SqlSession.class, sessionName);

        final SqlDynamicAttributes dynamicAttributes = new SqlDynamicAttributes(sqlSession, scopes);
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
