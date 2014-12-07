package io.soabase.example.mocks;

import io.soabase.sql.attributes.AttributeEntityMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;

public class MockDatabase implements Closeable
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public MockDatabase(SqlSession sqlSession)
    {
        log.info("Creating mock database...");
        AttributeEntityMapper mapper = sqlSession.getMapper(AttributeEntityMapper.class);
        mapper.createTable();
        log.info("done");
    }

    @Override
    public void close() throws IOException
    {
        log.info("Closing");
    }
}
