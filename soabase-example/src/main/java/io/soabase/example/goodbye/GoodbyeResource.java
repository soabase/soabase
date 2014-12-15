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

import io.soabase.client.SoaRequestId;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/goodbye")
public class GoodbyeResource
{
    @GET
    public String getGoodbye() throws Exception
    {
        return "goodbye " + SoaRequestId.get();
    }
}
