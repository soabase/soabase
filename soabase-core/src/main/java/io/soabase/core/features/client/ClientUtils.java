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
package io.soabase.core.features.client;

import com.google.common.base.Preconditions;
import io.soabase.core.features.discovery.Discovery;
import io.soabase.core.features.discovery.DiscoveryInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientUtils
{
    public static final String HOST_SUBSTITUTION_TOKEN = "0.";

    private static final Logger log = LoggerFactory.getLogger(ClientUtils.class);

    public static String hostToServiceName(String host)
    {
        host = Preconditions.checkNotNull(host, "Request URI's host cannot be null");
        if ( host.startsWith(HOST_SUBSTITUTION_TOKEN) && (host.length() > HOST_SUBSTITUTION_TOKEN.length()) )
        {
            return host.substring(HOST_SUBSTITUTION_TOKEN.length());
        }
        return null;
    }

    public static String serviceNameToUriForm(String serviceName)
    {
        return serviceNameToUriForm(serviceName, null);
    }

    public static String serviceNameToUriForm(String serviceName, String path)
    {
        return "//" + serviceNameToHost(serviceName, path);
    }

    public static String serviceNameToHost(String serviceName)
    {
        return serviceNameToHost(serviceName, null);
    }

    public static String serviceNameToHost(String serviceName, String path)
    {
        String host = HOST_SUBSTITUTION_TOKEN + serviceName;
        try
        {
            if ( serviceName.contains("/") )
            {
                throw new URISyntaxException(serviceName, "Cannot contain /");
            }
            new URI("http://" + host);
        }
        catch ( URISyntaxException e )
        {
            throw new RuntimeException("Invalid service name: " + serviceName, e);
        }
        if ( (path != null) && (path.length() > 0) )
        {
            if ( path.startsWith("/") )
            {
                path = path.substring(1);
            }
            return host + "/" + path;
        }
        return host;
    }

    public static DiscoveryInstance hostToInstance(Discovery discovery, String host)
    {
        String serviceName = hostToServiceName(host);
        if ( serviceName != null )
        {
            DiscoveryInstance instance = discovery.getInstance(serviceName);
            if ( instance == null )
            {
                throw new RuntimeException("Could not find an active instance for service: " + serviceName);
            }
            return instance;
        }
        return null;
    }

    public static URI applyToUri(URI uri, DiscoveryInstance instance)
    {
        if ( instance != null )
        {
            try
            {
                String scheme = instance.isForceSsl() ? "https" : ((uri.getScheme() != null) ? uri.getScheme() : "http");
                return new URI(scheme, uri.getUserInfo(), instance.getHost(), instance.getPort(), uri.getRawPath(), uri.getRawQuery(), uri.getRawFragment());
            }
            catch ( URISyntaxException e )
            {
                log.error("Could not parse uri", e);
                throw new RuntimeException(e);
            }
        }
        return uri;
    }

    private ClientUtils()
    {
    }
}
