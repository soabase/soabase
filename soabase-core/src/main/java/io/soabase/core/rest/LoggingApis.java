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

import io.soabase.core.SoaFeatures;
import io.soabase.core.features.logging.LoggingFile;
import io.soabase.core.features.logging.LoggingReader;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/soa/logging")
public class LoggingApis
{
    private final LoggingReader loggingReader;

    @Inject
    public LoggingApis(SoaFeatures features)
    {
        loggingReader = features.getLoggingReader();
    }

    @GET
    @Path("files")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LoggingFile> listLoggingFiles()
    {
        return loggingReader.listLoggingFiles();
    }

    @GET
    @Path("file/gzip/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFileZipped(@PathParam("key") String key)
    {
        Object entity = loggingReader.keyToEntity(key, true);
        if ( entity == null )
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).encoding("gzip").build();
    }

    @GET
    @Path("file/raw/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFile(@PathParam("key") String key)
    {
        Object entity = loggingReader.keyToEntity(key, false);
        if ( entity == null )
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }
}
