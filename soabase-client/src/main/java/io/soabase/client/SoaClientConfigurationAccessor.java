package io.soabase.client;

import io.dropwizard.client.HttpClientConfiguration;

public interface SoaClientConfigurationAccessor
{
    public HttpClientConfiguration getHttpClientConfiguration();
}
