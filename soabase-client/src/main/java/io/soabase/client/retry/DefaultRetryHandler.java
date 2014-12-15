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
package io.soabase.client.retry;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.soabase.client.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ConnectException;

@JsonTypeName("default")
public class DefaultRetryHandler implements RetryHandler
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean shouldBeRetried(RetryContext retryContext, int retryCount, int statusCode, Throwable exception)
    {
        if ( retryCount >= retryContext.getComponents().getRetries() )
        {
            log.warn(String.format("Retries exceeded. retryCount: %d - maxRetries: %d", retryCount, retryContext.getComponents().getRetries()));
            return false;
        }

        if ( (statusCode != 0) && retryContext.getComponents().isRetry500s() )
        {
            if ( (statusCode >= 500) && (statusCode <= 599) )
            {
                exception = new IOException("Internal Server Error: " + statusCode);
            }
        }
        boolean shouldBeRetried = shouldBeRetried(retryContext, exception);
        if ( shouldBeRetried )
        {
            String serviceName = Common.hostToServiceName(retryContext.getOriginalHost());
            if ( (serviceName != null) && (retryContext.getInstance() != null) )
            {
                retryContext.getComponents().getDiscovery().noteError(serviceName, retryContext.getInstance());
            }
        }
        return shouldBeRetried;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean shouldBeRetried(RetryContext retryContext, Throwable exception)
    {
        if ( exception == null )
        {
            return false;
        }

        boolean retry = false;
        if ( exception instanceof ConnectException )
        {
            retry = true;
        }
        else if ( isIdempotentMethod(retryContext.getMethod()) )
        {
            retry = true;
        }

        if ( retry && (exception instanceof IOException) )
        {
            log.info(String.format("Retrying request due to exception %s. request: %s", exception.getClass().getSimpleName(), retryContext.getOriginalUri()));
            return true;
        }

        return shouldBeRetried(retryContext, exception.getCause());
    }

    private boolean isIdempotentMethod(String method)
    {
        return method.equalsIgnoreCase("get") || method.equalsIgnoreCase("put");
    }
}
