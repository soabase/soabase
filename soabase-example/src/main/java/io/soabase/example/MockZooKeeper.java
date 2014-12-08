package io.soabase.example;

import org.apache.curator.test.TestingServer;

public class MockZooKeeper
{
    public static void main(String[] args) throws Exception
    {
        new TestingServer(2181);
        Thread.currentThread().join();
    }
}
