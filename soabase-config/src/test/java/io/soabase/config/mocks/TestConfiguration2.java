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
