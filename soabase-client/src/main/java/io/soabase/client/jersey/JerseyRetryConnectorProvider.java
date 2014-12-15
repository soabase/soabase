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
package io.soabase.client.jersey;

import io.soabase.client.retry.RetryComponents;
import io.soabase.core.features.discovery.SoaDiscovery;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

public class JerseyRetryConnectorProvider implements ConnectorProvider
{
    private final SoaDiscovery discovery;
    private final RetryComponents retryComponents;

    public JerseyRetryConnectorProvider(SoaDiscovery discovery, RetryComponents retryComponents)
    {
        this.discovery = discovery;
        this.retryComponents = retryComponents;
    }

    @Override
    public Connector getConnector(Client client, Configuration runtimeConfig)
    {
        return new JerseyRetryConnector(client, retryComponents, runtimeConfig);
    }
}
