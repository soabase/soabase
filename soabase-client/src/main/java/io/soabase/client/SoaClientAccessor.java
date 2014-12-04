package io.soabase.client;

import io.dropwizard.client.HttpClientConfiguration;

public interface SoaClientAccessor
{
    public HttpClientConfiguration getHttpClientConfiguration();
}
