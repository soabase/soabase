package io.soabase.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.client.JerseyClientConfiguration;
import javax.validation.Valid;
import javax.validation.constraints.Min;

public class SoaClientConfiguration
{
    @Valid
    private HttpClientConfiguration httpClientConfiguration = null;

    @Valid
    private JerseyClientConfiguration jerseyClientConfiguration = new JerseyClientConfiguration();

    @Valid
    private int maxRetries = 3;

    @Valid
    private boolean retry500s = true;

    @JsonProperty("maxRetries")
    @Min(0)
    public int getMaxRetries()
    {
        return maxRetries;
    }

    @JsonProperty("maxRetries")
    @Min(0)
    public void setMaxRetries(int maxRetries)
    {
        this.maxRetries = maxRetries;
    }

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

    @JsonProperty("retry500s")
    public boolean isRetry500s()
    {
        return retry500s;
    }

    @JsonProperty("retry500s")
    public void setRetry500s(boolean retry500s)
    {
        this.retry500s = retry500s;
    }
}
