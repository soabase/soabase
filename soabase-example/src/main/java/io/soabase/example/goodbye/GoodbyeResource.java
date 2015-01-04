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
package io.soabase.example.goodbye;

import io.soabase.core.SoaFeatures;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.client.RequestId;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

@Path("/goodbye")
public class GoodbyeResource
{
    private final SoaInfo info;

    @Inject
    public GoodbyeResource(SoaFeatures features)
    {
        this.info = features.getSoaInfo();
    }

    @GET
    public String getGoodbye(@Context HttpHeaders headers) throws Exception
    {
        return "Service Name: " + info.getServiceName()
            + "\n\tInstance Name: " + info.getInstanceName()
            + "\n\tRequest Id: " + RequestId.get(headers)
            + "\n"
            ;
    }
}
