package io.soabase.admin.components;

public class Metric
{
    private final String path;
    private final String label;

    public Metric(String label, String path)
    {
        this.path = path;
        this.label = label;
    }

    public String getPath()
    {
        return path;
    }

    public String getLabel()
    {
        return label;
    }
}
