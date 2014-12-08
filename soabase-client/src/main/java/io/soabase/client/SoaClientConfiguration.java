package io.soabase.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.client.JerseyClientConfiguration;
import javax.validation.Valid;

public class SoaClientConfiguration
{
    @Valid
    private HttpClientConfiguration httpClientConfiguration = null;

    @Valid
    private JerseyClientConfiguration jerseyClientConfiguration = new JerseyClientConfiguration();

    @JsonProperty("apache")
    public HttpClientConfiguration getHttpClientConfiguration()
    {
        return httpClientConfiguration;
    }

    @JsonProperty("apache")
    public void setHttpClientConfiguration(HttpClientConfiguration httpClientConfiguration)
    {
        this.httpClientConfiguration = httpClientConfiguration;
    }

    @JsonProperty("jersey")
    public JerseyClientConfiguration getJerseyClientConfiguration()
    {
        return jerseyClientConfiguration;
    }

    @JsonProperty("jersey")
    public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClientConfiguration)
    {
        this.jerseyClientConfiguration = jerseyClientConfiguration;
    }
}
