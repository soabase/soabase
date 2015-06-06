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
package io.soabase.sql.mocks;

import org.hsqldb.server.Server;
import java.io.Closeable;
import java.io.IOException;

public class MockDatabase implements Closeable
{
    private final Server server;

    public MockDatabase()
    {
        this.server = new Server();
        server.start();
    }

    @Override
    public void close() throws IOException
    {
        server.stop();
    }
}
