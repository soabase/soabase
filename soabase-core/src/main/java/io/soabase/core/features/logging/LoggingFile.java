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
