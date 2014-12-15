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

import io.dropwizard.lifecycle.setup.ExecutorServiceBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class RetryExecutor
{
    private final ExecutorServiceBuilder builder;
    private final AtomicReference<ExecutorService> executorService = new AtomicReference<>();

    private volatile Accessor accessor = new Accessor() // a Thunk
    {
        @Override
        public ExecutorService getExecutorService()
        {
            synchronized(RetryExecutor.this)
            {
                if ( executorService.get() == null )
                {
                    executorService.set(builder.build());
                }
                accessor = new Accessor()
                {
                    @Override
                    public ExecutorService getExecutorService()
                    {
                        return executorService.get();
                    }
                };
                return executorService.get();
            }
        }
    };

    private interface Accessor
    {
        ExecutorService getExecutorService();
    }

    public RetryExecutor(ExecutorServiceBuilder builder)
    {
        this.builder = builder;
    }

    public ExecutorService getExecutorService()
    {
        return accessor.getExecutorService();
    }
}
