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

/**
 * <p>
 *     Utility that enables specifying command line arguments that can be a
 *     resource contained in a JAR or in the file system. The extractor will
 *     prefer the file system. But, if not present (or not specified) it
 *     will create a temp file containing the resource from the JAR adjusting
 *     the command line arguments.
 * </p>
 *
 * <p>
 *     Only arguments containing the specified separator are processed. The default
 *     separator is '!'. The format for processed arguments is [file]SEPARATOR[resource].
 *     E.g. "config/app.prefs!config/default/app.prefs". The file portion is optional e.g.
 *     "!config/default/app.prefs". When the argument is processed, the file system is checked for
 *     the file (if specified) and, if found, the argument is replaced with the file path. Otherwise,
 *     the resource is extracted from the classpath (via {@link Resources#getResource(String)}) and
 *     written to a temp file. The argument is replaced with the path to the temp file.
 * </p>
 */
public class JarFileExtractor
{
    private final String separator;

    public static final String DEFAULT_SEPARATOR = "!";

    /**
     * Convenience method. Creates a {@link JarFileExtractor} using the
     * {@link JarFileExtractor#DEFAULT_SEPARATOR} and returns the filtered
     * arguments
     *
     * @param args arguments
     * @return filtered arguments
     * @throws IOException errors
     */
    public static String[] filter(String... args) throws IOException
    {
        return new JarFileExtractor().filterArguments(args);
    }

    /**
     * An extractor using the {@link #DEFAULT_SEPARATOR}
     */
    public JarFileExtractor()
    {
        this(DEFAULT_SEPARATOR);
    }

    /**
     * An extractor with the given separator
     *
     * @param separator separates the file portion and the resource portion. Only
     *                  arguments that contain the separator will be processed
     */
    public JarFileExtractor(String separator)
    {
        this.separator = separator;
    }

    /**
     * Process the given argument.
     *
     * @param arg argument
     * @return If the separator is present in the argument, either the file or temp file otherwise returns the given argument
     * @throws IOException errors
     */
    public String filterArgument(String arg) throws IOException
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
        return arg;
    }

    /**
     * Process an array of arguments passing each argument to {@link #filterArgument(String)}
     *
     * @param args arguments
     * @return filtered arguments
     * @throws IOException errors
     */
    public String[] filterArguments(String... args) throws IOException
    {
        List<String> filtered = Lists.newArrayList();
        for ( String arg : args )
        {
            filtered.add(filterArgument(arg));
        }

        return filtered.toArray(new String[filtered.size()]);
    }
}
