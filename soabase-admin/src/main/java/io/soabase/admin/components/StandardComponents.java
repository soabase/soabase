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
package io.soabase.admin.components;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.util.List;
import java.util.UUID;

public class StandardComponents
{
    public static void main(String[] args)
    {
        String id = UUID.randomUUID().toString();
        for ( int i = 1; i <= 100; ++i )
        {
            System.out.println(Hashing.consistentHash(Hashing.sha256().hashString(id, Charsets.UTF_8), i));
        }
    }

    public static MetricComponent newGcMetric()
    {
        // TODO - get all collectors
        List<Metric> metrics = Lists.newArrayList
        (
            new Metric("MarkSweep", "gauges['jvm.gc.PS-MarkSweep.count'].value"),
            new Metric("MarkSweep", "gauges['jvm.gc.MarkSweepCompact.count'].value"),
            new Metric("MarkSweep", "gauges['jvm.gc.PS-ConcurrentMarkSweep.count'].value"),
            new Metric("MarkSweep", "gauges['jvm.gc.PSYoungGen.count'].value"),
            new Metric("MarkSweep", "gauges['jvm.gc.PSOldGen.count'].value"),
            new Metric("Scavenge", "gauges['jvm.gc.PS-Scavenge.count'].value")
        );
        return new MetricComponent("soa-gc", MetricType.DELTA, "GC", "# of GCs", metrics);
    }

    public static MetricComponent newGcTimesMetric()
    {
        // TODO - get all collectors
        List<Metric> metrics = Lists.newArrayList
            (
                new Metric("MarkSweep", "gauges['jvm.gc.PS-MarkSweep.time'].value"),
                new Metric("MarkSweep", "gauges['jvm.gc.MarkSweepCompact.time'].value"),
                new Metric("MarkSweep", "gauges['jvm.gc.ConcurrentMarkSweep.time'].value"),
                new Metric("MarkSweep", "gauges['jvm.gc.PSYoungGen.time'].value"),
                new Metric("MarkSweep", "gauges['jvm.gc.PSOldGen.time'].value"),
                new Metric("Scavenge", "gauges['jvm.gc.PS-Scavenge.time'].value")
            );
        return new MetricComponent("soa-gc-times", MetricType.DELTA, "GC Times", "Time", metrics);
    }

    public static MetricComponent newHeapMetric()
    {
        List<Metric> metrics = Lists.newArrayList
            (
                new Metric("Heap", "gauges['jvm.memory.heap.usage'].value")
            );
        return new MetricComponent("soa-heap", MetricType.PERCENT, "Heap", "% Used", metrics);
    }

    public static MetricComponent newThreadsMetric()
    {
        List<Metric> metrics = Lists.newArrayList
        (
            new Metric("Threads", "gauges['jvm.threads.count'].value"),
            new Metric("Blocked", "gauges['jvm.threads.blocked.count'].value")
        );
        return new MetricComponent("soa-threads", MetricType.STANDARD, "Threads", "Count", metrics);
    }

    public static MetricComponent newCpuMetric()
    {
        List<Metric> metrics = Lists.newArrayList
        (
            new Metric("CPU", "gauges['system.cpu.load'].value")
        );
        return new MetricComponent("soa-cpu", MetricType.PERCENT, "System CPU", "% Load", metrics);
    }

    public static MetricComponent newRequestsMetric()
    {
        List<Metric> metrics = Lists.newArrayList
        (
            new Metric("Active Requests", "counters['io.dropwizard.jetty.MutableServletContextHandler.active-requests'].count")
        );
        return new MetricComponent("soa-requests", MetricType.STANDARD, "Active Requests", "Count", metrics);
    }

    public static MetricComponent newRequestStatusMetric()
    {
        List<Metric> metrics = Lists.newArrayList
        (
            new Metric("100s", "meters['io.dropwizard.jetty.MutableServletContextHandler.1xx-responses'].count"),
            new Metric("200s", "meters['io.dropwizard.jetty.MutableServletContextHandler.2xx-responses'].count"),
            new Metric("300s", "meters['io.dropwizard.jetty.MutableServletContextHandler.3xx-responses'].count"),
            new Metric("400s", "meters['io.dropwizard.jetty.MutableServletContextHandler.4xx-responses'].count"),
            new Metric("500s", "meters['io.dropwizard.jetty.MutableServletContextHandler.5xx-responses'].count")
        );
        return new MetricComponent("soa-request-status", MetricType.DELTA, "Statuses", "Count", metrics);
    }

    public static TabComponent newServicesTab()
    {
        return TabComponentBuilder.builder()
            .withId("soa-services")
            .withName("Services")
            .withContentResourcePath("assets/services/services.html")
            .addingJavascriptUriPath("/assets/services/js/services.js")
            .addingCssUriPath("/assets/services/css/services.css")
            .addingAssetsPath("/assets/services/js")
            .addingAssetsPath("/assets/services/css")
            .build();
    }

    public static TabComponent newAttributesTab()
    {
        return TabComponentBuilder.builder()
            .withId("soa-attributes")
            .withName("Attributes")
            .withContentResourcePath("assets/attributes/attributes.html")
            .addingJavascriptUriPath("/assets/attributes/js/attributes.js")
            .addingCssUriPath("/assets/attributes/css/attributes.css")
            .addingAssetsPath("/assets/attributes/js")
            .addingAssetsPath("/assets/attributes/css")
            .build();
    }

    private StandardComponents()
    {
    }
}
