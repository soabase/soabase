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
