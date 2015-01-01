package io.soabase.admin.components;

public class MetricComponent implements ComponentId
{
    private final String id;
    private final MetricType type;
    private final String name;
    private final String prefix;
    private final String suffix;
    private final String label;

    public MetricComponent(String id, MetricType type, String name, String prefix, String suffix, String label)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.label = label;
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

    public String getPrefix()
    {
        return prefix;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public String getLabel()
    {
        return label;
    }

    public MetricType getType()
    {
        return type;
    }
}
