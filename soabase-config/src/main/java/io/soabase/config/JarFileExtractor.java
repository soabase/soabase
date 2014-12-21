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
package io.soabase.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class JarFileExtractor
{
    private final String separator;

    public static final String DEFAULT_SEPARATOR = "!";

    public static String[] filter(String... args) throws IOException
    {
        return new JarFileExtractor(DEFAULT_SEPARATOR).filterAndExtract(args);
    }

    public JarFileExtractor(String separator)
    {
        this.separator = separator;
    }

    public String[] filterAndExtract(String... args) throws IOException
    {
        List<String> filtered = Lists.newArrayList();
        for ( String arg : args )
        {
            if ( arg.contains(separator) )
            {
                List<String> parts = Splitter.on(separator).trimResults().limit(2).splitToList(arg);
                if ( parts.size() == 2 )
                {
                    File filePart = new File(parts.get(0));
                    String resourcePart = parts.get(1);
                    if ( filePart.exists() )
                    {
                        arg = filePart.getCanonicalPath();
                    }
                    else
                    {
                        URL resource = Resources.getResource(resourcePart);
                        File f = File.createTempFile("soa", ".tmp");
                        f.deleteOnExit();
                        try ( OutputStream out = new BufferedOutputStream(new FileOutputStream(f)) )
                        {
                            Resources.copy(resource, out);
                        }
                        arg = f.getCanonicalPath();
                    }
                }
            }
            filtered.add(arg);
        }

        return filtered.toArray(new String[filtered.size()]);
    }
}
