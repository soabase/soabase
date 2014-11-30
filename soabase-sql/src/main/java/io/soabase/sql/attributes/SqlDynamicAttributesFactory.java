package io.soabase.sql.attributes;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.setup.Environment;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.SoaDynamicAttributesFactory;

@JsonTypeName("sql")
public class SqlDynamicAttributesFactory implements SoaDynamicAttributesFactory
{
    @Override
    public SoaDynamicAttributes build(Environment environment, String groupName, String instanceName)
    {
        return new SqlDynamicAttributes(groupName, instanceName);
    }
}
