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
package io.soabase.guice;

import com.google.common.io.CharStreams;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.soabase.guice.mocks.MockApplication;
import io.soabase.guice.mocks.MockGuiceInjected;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestGuiceBundle
{
    @Test
    public void testIt() throws Exception
    {
        Module module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind(MockGuiceInjected.class).asEagerSingleton();
            }
        };
        Injector injector = Guice.createInjector(module);
        final MockApplication mockApplication = new MockApplication(injector);

        Callable callable = new Callable()
        {
            @Override
            public Object call() throws Exception
            {
                String[] args = {"server"};
                mockApplication.run(args);
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
        try
        {
            Assert.assertTrue(mockApplication.getStartedLatch().await(5, TimeUnit.SECONDS));
            URI uri = new URI("http://localhost:8080/test");
            String str = CharStreams.toString(new InputStreamReader(uri.toURL().openStream()));
            Assert.assertEquals(str, "guice - hk2");
        }
        finally
        {
            future.cancel(true);
            ShutdownThread.getInstance().run();
        }
    }
}
