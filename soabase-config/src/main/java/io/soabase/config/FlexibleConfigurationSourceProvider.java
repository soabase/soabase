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
import com.google.common.io.Resources;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * <p>
 *     A flexible Dropwizard {@link ConfigurationSourceProvider}. Allows configuration
 *     to be either a string, an external file or a resource in the classpath. Special
 *     tokens are used to determine how to treat the argument.
 * </p>
 *
 * <p>
 *     If the argument is prefixed
 *     with the "string prefix" (<tt>%</tt> by default) the argument is passed as the configuration value.
 * </p>
 *
 * <p>
 *     The argument
 *     can also contain the "resource separator" (<tt>|</tt> by default) formatted as <tt>[file]SEPARATOR[resource]</tt>.
 *     E.g. "config/app.prefs|config/default/app.prefs". The file portion is optional e.g.
 *     "|config/default/app.prefs". When the argument is processed, the file system is checked for
 *     the file (if specified) and, if found, is used as the configuration file. Otherwise,
 *     the resource is determined from the classpath (via {@link Resources#getResource(String)})
 *     and used as the configuration file.
 * </p>
 */
public class FlexibleConfigurationSourceProvider extends FileConfigurationSourceProvider
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String resourceSeparator;
    private final String stringPrefix;

    public static final String DEFAULT_RESOURCE_SEPARATOR = "|";
    public static final String DEFAULT_STRING_PREFIX = "%";

    public FlexibleConfigurationSourceProvider()
    {
        this(DEFAULT_RESOURCE_SEPARATOR, DEFAULT_STRING_PREFIX);
    }

    public FlexibleConfigurationSourceProvider(String resourceSeparator, String stringPrefix)
    {
        this.resourceSeparator = resourceSeparator;
        this.stringPrefix = stringPrefix;
    }

    @Override
    public InputStream open(String value) throws IOException
    {
        InputStream stream = null;
        do
        {
            if ( value.startsWith(stringPrefix) )
            {
                log.info("Configuration argument is a string literal");
                String str = (value.length() > stringPrefix.length()) ? value.substring(stringPrefix.length()) : "";
                stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
                break;
            }

            if ( value.contains(resourceSeparator) )
            {
                List<String> parts = Splitter.on(resourceSeparator).trimResults().limit(2).splitToList(value);
                if ( parts.size() == 2 )
                {
                    String pathname = parts.get(0);
                    File filePart = new File(pathname);
                    String resourcePart = parts.get(1);
                    if ( (pathname.length() > 0) && filePart.exists() )
                    {
                        value = filePart.getCanonicalPath();
                        log.info("Using " + value + " for configuration");
                    }
                    else
                    {
                        log.info("Using " + resourcePart + " for configuration");
                        URL resource = Resources.getResource(resourcePart);
                        stream = resource.openStream();
                    }
                }

            }
        } while ( false );

        return (stream != null) ? stream : super.open(value);
    }
}
