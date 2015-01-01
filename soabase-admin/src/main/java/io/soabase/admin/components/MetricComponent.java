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

import com.google.common.collect.ImmutableList;
import java.util.List;

public class MetricComponent implements ComponentId
{
    private final String id;
    private final MetricType type;
    private final String name;
    private final String label;
    private final List<Metric> metrics;

    public MetricComponent(String id, MetricType type, String name, String label, List<Metric> metrics)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.label = label;
        this.metrics = ImmutableList.copyOf(metrics);
    }

    @Override
    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public MetricType getType()
    {
        return type;
    }

    public List<Metric> getMetrics()
    {
        return metrics;
    }
}
