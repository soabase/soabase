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
package io.soabase.core.config;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class SoaCli
{
    public static String[] filter(String... args) throws IOException
    {
        List<String> filtered = Lists.newArrayList();
        for ( String arg : args )
        {
            if ( arg.startsWith("!") && (arg.length() > 0) )
            {
                System.out.println("Unpacking: " + arg.substring(1));
                URL resource = Resources.getResource(arg.substring(1));
                File f = File.createTempFile("soa", ".tmp");
                f.deleteOnExit();
                try ( OutputStream out = new BufferedOutputStream(new FileOutputStream(f)) )
                {
                    Resources.copy(resource, out);
                }
                arg = f.getCanonicalPath();
            }
            filtered.add(arg);
        }

        return filtered.toArray(new String[filtered.size()]);
    }

    private SoaCli()
    {
    }
}
