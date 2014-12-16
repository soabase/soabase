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
package io.soabase.core.rest;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import io.soabase.core.SoaInfo;
import io.soabase.core.rest.entities.Info;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.TimeZone;

@Path("/soa")
public class SoaApis
{
    private final SoaInfo soaInfo;

    @Inject
    public SoaApis(SoaInfo soaInfo)
    {
        this.soaInfo = soaInfo;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Info getInfo()
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        ISO8601DateFormat df = new ISO8601DateFormat();
        df.setTimeZone(tz);
        String now = df.format(new Date());
        String start = df.format(new Date(soaInfo.getStartTimeMs()));
        return new Info(soaInfo.getScopes(), soaInfo.getMainPort(), soaInfo.getAdminPort(), soaInfo.getServiceName(), soaInfo.getInstanceName(), start, now);
    }
}
