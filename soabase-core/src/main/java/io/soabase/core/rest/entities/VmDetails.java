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
package io.soabase.core.rest.entities;

import com.google.common.collect.Maps;
import java.util.Map;

public class VmDetails
{
    private String stackTrace;
    private Map<String, Long> data;

    public VmDetails()
    {
        this("", Maps.<String, Long>newHashMap());
    }

    public VmDetails(String stackTrace, Map<String, Long> data)
    {
        this.stackTrace = stackTrace;
        this.data = data;
    }

    public String getStackTrace()
    {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace)
    {
        this.stackTrace = stackTrace;
    }

    public Map<String, Long> getData()
    {
        return data;
    }

    public void setData(Map<String, Long> data)
    {
        this.data = data;
    }
}
