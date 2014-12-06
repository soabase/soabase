package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import io.dropwizard.setup.Environment;
import io.soabase.core.SoaConfiguration;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;
import org.apache.ibatis.session.SqlSession;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@JsonTypeName("sql")
public class SqlDynamicAttributesFactory implements SoaDynamicAttributesFactory
{
    @Valid
    private int refreshPeriodSeconds = 30;

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
    public SoaDynamicAttributes build(SoaConfiguration configuration, Environment environment, List<String> scopes)
    {
        SqlSession sqlSession = Preconditions.checkNotNull(SoaSqlBundle.getSqlSession(configuration), "SoaSqlBundle has not been added or initialized");

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
