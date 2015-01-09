package io.soabase.admin.details;

public class IndexMapping
{
    private final String path;
    private final String file;
    private final String key;
    private final boolean isAuthServlet;

    public IndexMapping(String path, String file)
    {
        this(path, file, false);
    }

    public IndexMapping(String path, String file, boolean isAuthServlet)
    {
        this.isAuthServlet = isAuthServlet;
        this.key = path.equals("") ? "/" : path;
        this.path = path;
        this.file = file;
    }

    public String getPath()
    {
        return path;
    }

    public String getFile()
    {
        return file;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isAuthServlet()
    {
        return isAuthServlet;
    }
}
