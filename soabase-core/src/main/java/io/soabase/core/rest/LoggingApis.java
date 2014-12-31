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

import com.google.common.collect.Maps;
import io.soabase.core.SoaFeatures;
import io.soabase.core.features.logging.LoggingFile;
import io.soabase.core.features.logging.LoggingReader;
import io.soabase.core.rest.entities.VmDetails;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Map;

@Path("/soa/logging")
public class LoggingApis
{
    private final LoggingReader loggingReader;
    
    private static final int MAX_DEPTH = 50;

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

    @GET
    @Path("stack")
    @Produces(MediaType.TEXT_PLAIN)
    public String getStackTrace()
    {
        return buildStackTrace();
    }

    @GET
    @Path("vm")
    @Produces(MediaType.APPLICATION_JSON)
    public VmDetails getVmDetails()
    {
        String stackTrace = buildStackTrace();

        Map<String, Long> data = Maps.newHashMap();

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        addMemoryUsage(data, "heap", memoryMXBean.getHeapMemoryUsage());

        for ( GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans() )
        {
            data.put(gcBean.getName() + "-gc-count", gcBean.getCollectionCount());
            data.put(gcBean.getName() + "-gc-time", gcBean.getCollectionTime());
        }

        data.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());

        return new VmDetails(stackTrace, data);
    }

    private String buildStackTrace()
    {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        StringBuilder stackTrace = new StringBuilder();
        for ( long id : threadMXBean.getAllThreadIds() )
        {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(id, MAX_DEPTH);
            if ( threadInfo != null )
            {
                stackTrace.append(threadInfo.toString()).append("\n");
            }
        }

        stackTrace.append("\n");
        return stackTrace.toString();
    }

    private void addMemoryUsage(Map<String, Long> data, String prefix, MemoryUsage memoryUsage)
    {
        data.put(prefix + "-memory-committed", memoryUsage.getCommitted());
        data.put(prefix + "-memory-init", memoryUsage.getInit());
        data.put(prefix + "-memory-max", memoryUsage.getMax());
        data.put(prefix + "-memory-used", memoryUsage.getUsed());
    }
}
