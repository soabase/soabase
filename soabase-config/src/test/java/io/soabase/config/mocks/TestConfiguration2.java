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
package io.soabase.config.mocks;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestConfiguration2
{
    private String field1 = "a";
    private String field2 = "b";

    @JsonProperty("a")
    public String getField1()
    {
        return field1;
    }
    @JsonProperty("a")

    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    @JsonProperty("b")
    public String getField2()
    {
        return field2;
    }

    @JsonProperty("b")
    public void setField2(String field2)
    {
        this.field2 = field2;
    }
}
