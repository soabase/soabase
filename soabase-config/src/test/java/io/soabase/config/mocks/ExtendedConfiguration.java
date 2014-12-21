package io.soabase.config.mocks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.soabase.config.ComposedConfiguration;

public class ExtendedConfiguration extends ComposedConfiguration
{
    private String field1 = "1";
    private String field2 = "2";

    @JsonProperty("one")
    public String getField1()
    {
        return field1;
    }
    @JsonProperty("one")

    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    @JsonProperty("two")
    public String getField2()
    {
        return field2;
    }

    @JsonProperty("two")
    public void setField2(String field2)
    {
        this.field2 = field2;
    }
}
