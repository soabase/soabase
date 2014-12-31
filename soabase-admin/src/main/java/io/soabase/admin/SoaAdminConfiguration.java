/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.soabase.core.SoaConfiguration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SoaAdminConfiguration extends Configuration
{
    @Valid
    @NotNull
    private String appName = "Soabase";

    @Valid
    @NotNull
    private String company = "";

    @Valid
    @NotNull
    private String footerMessage = "- Internal use only - Proprietary and Confidential";

    @Valid
    @NotNull
    private SoaConfiguration soa = new SoaConfiguration();

    @JsonProperty("appName")
    public String getAppName()
    {
        return appName;
    }

    @JsonProperty("appName")
    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    @JsonProperty("company")
    public String getCompany()
    {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(String company)
    {
        this.company = company;
    }

    @JsonProperty("footerMessage")
    public String getFooterMessage()
    {
        return footerMessage;
    }

    @JsonProperty("footerMessage")
    public void setFooterMessage(String footerMessage)
    {
        this.footerMessage = footerMessage;
    }

    @JsonProperty("soa")
    public SoaConfiguration getSoa()
    {
        return soa;
    }

    @JsonProperty("soa")
    public void setSoa(SoaConfiguration soa)
    {
        this.soa = soa;
    }
}
