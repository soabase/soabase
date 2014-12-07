package io.soabase.example.mocks;

import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;

public class MockZooKeeper implements Closeable
{
    private final TestingServer server;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public MockZooKeeper()
    {
        try
        {
            log.info("Creating mock ZooKeeper...");
            server = new TestingServer();
            log.info("done");
        }
        catch ( Exception e )
        {
            throw new RuntimeException(e);
        }
    }

    public String getConnectionString()
    {
        return server.getConnectString();
    }

    @Override
    public void close() throws IOException
    {
        log.info("Closing...");
        server.close();
        log.info("done");
    }
}
