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

import org.eclipse.jetty.util.thread.ShutdownThread;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestClientRetries
{
    private MockApplication application;

    @Before
    public void setupMethod() throws Exception
    {
        application = new MockApplication();
        application.run("server");
        application.getStartedLatch().await();
        application.getThrowInternalError().set(true);
        application.getCounter().set(0);
    }

    @After
    public void tearDown()
    {
        ShutdownThread.getInstance().run();
    }

    @Test
    public void testSyncRetry()
    {
        String value = application.getClient().target("http://localhost:8080/test").request().get(String.class);
        Assert.assertEquals("test", value);
        Assert.assertEquals(application.getCounter().get(), 2);
    }

    @Test
    public void testAsyncRetry() throws ExecutionException, InterruptedException
    {
        Future<String> future = application.getClient().target("http://localhost:8080/test").request().async().get(String.class);
        String value = future.get();
        Assert.assertEquals("test", value);
        Assert.assertEquals(2, application.getCounter().get());
    }
}
