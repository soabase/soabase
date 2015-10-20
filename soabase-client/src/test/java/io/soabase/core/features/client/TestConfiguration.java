package io.soabase.core.features.client;

import io.dropwizard.Configuration;
import io.soabase.core.features.config.SoaConfiguration;
import javax.validation.Valid;

public class TestConfiguration extends Configuration
{
    @Valid
    public SoaConfiguration soa = new SoaConfiguration();

    public TestConfiguration()
    {
        soa.setServiceName("test");
    }
}
