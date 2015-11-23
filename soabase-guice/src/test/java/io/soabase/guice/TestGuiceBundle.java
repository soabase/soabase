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
import com.google.inject.Module;
import io.soabase.guice.mocks.JerseyGuiceResource;
import io.soabase.guice.mocks.MockApplication;
import io.soabase.guice.mocks.MockFilter;
import io.soabase.guice.mocks.MockGuiceInjected;
import io.soabase.guice.mocks.MockHK2Injected;
import io.soabase.guice.mocks.MockOldStyleApplication;
import io.soabase.guice.mocks.MockResource;
import io.soabase.guice.mocks.MockServlet;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
        final MockApplication mockApplication = new MockApplication(module);

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
            Assert.assertEquals("guice - hk2", str);
        }
        finally
        {
            future.cancel(true);
            ShutdownThread.getInstance().run();
        }
    }

    @Test
    public void testJerseyMultiServletModule() throws Exception
    {
        Module module = new JerseyGuiceModule()
        {
            @Override
            protected void configureServlets()
            {
                bind(MockResource.class);
                bind(MockHK2Injected.class).asEagerSingleton();
                bind(MockGuiceInjected.class).asEagerSingleton();

                filter("/*").through(MockFilter.class);
            }
        };

        Module module2 = new JerseyMultiGuiceModule()
        {
            @Override
            protected void configureServlets()
            {
                serve("/mock/test").with(MockServlet.class);
                bind(JerseyGuiceResource.class);
            }
        };

        runTest(module, module2);
    }

    @Test
    public void testJerseyServletModule() throws Exception
    {
        Module module = new JerseyGuiceModule()
        {
            @Override
            protected void configureServlets()
            {
                bind(MockResource.class);
                bind(MockHK2Injected.class).asEagerSingleton();
                bind(MockGuiceInjected.class).asEagerSingleton();

                filter("/*").through(MockFilter.class);
                serve("/mock/test").with(MockServlet.class);

                bind(JerseyGuiceResource.class);
            }
        };

        runTest(module);
    }

    private void runTest(Module... modules) throws InterruptedException, URISyntaxException, IOException
    {
        final MockOldStyleApplication mockApplication = new MockOldStyleApplication(modules);
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
            Assert.assertEquals("success - guice - hk2", str);

            uri = new URI("http://localhost:8080/mock/test");
            str = CharStreams.toString(new InputStreamReader(uri.toURL().openStream()));
            Assert.assertEquals("hello", str);

            uri = new URI("http://localhost:8080/jg");
            str = CharStreams.toString(new InputStreamReader(uri.toURL().openStream()));
            Assert.assertEquals("jg", str);
        }
        finally
        {
            future.cancel(true);
            ShutdownThread.getInstance().run();
        }
    }
}
