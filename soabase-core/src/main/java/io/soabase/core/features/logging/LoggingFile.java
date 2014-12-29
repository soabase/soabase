package io.soabase.core.features.logging;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import java.io.File;

public class LoggingFile
{
    private final String key;
    private final String name;
    private final File file;

    public LoggingFile(File file)
    {
        this.file = Preconditions.checkNotNull(file, "file cannot be null");
        this.key = Hashing.sha1().hashString(file.getPath(), Charsets.UTF_8).toString();
        this.name = file.getName();
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public File getFile()
    {
        return file;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        LoggingFile that = (LoggingFile)o;

        if ( !key.equals(that.key) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !name.equals(that.name) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "LoggingFile{" +
            "key='" + key + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
