package io.soabase.client;

import com.google.common.base.Preconditions;
import io.soabase.core.features.discovery.SoaDiscovery;
import io.soabase.core.features.discovery.SoaDiscoveryInstance;
import java.net.URI;
import java.net.URISyntaxException;

public class Common
{
    public static SoaDiscoveryInstance hostToInstance(SoaDiscovery discovery, String host)
    {
        host = Preconditions.checkNotNull(host, "Request URI's host cannot be null");
        if ( host.startsWith(SoaClientBundle.HOST_SUBSTITUTION_TOKEN) && (host.length() > SoaClientBundle.HOST_SUBSTITUTION_TOKEN.length()) )
        {
            String serviceName = host.substring(SoaClientBundle.HOST_SUBSTITUTION_TOKEN.length());
            return Preconditions.checkNotNull(discovery.getInstance(serviceName), "No instance found for " + serviceName);
        }
        return null;
    }

    public static URI filterUri(URI uri, SoaDiscoveryInstance instance)
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
                // TODO logging
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private Common()
    {
    }
}
