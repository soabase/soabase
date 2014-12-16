package io.soabase.example;

import com.google.common.io.Resources;
import io.soabase.sql.attributes.AttributeEntityMapper;
import org.apache.curator.test.DirectoryUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hsqldb.server.Server;
import java.io.File;
import java.io.InputStream;

public class MockDatabase
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        File db = new File("testdb");
        if ( db.exists() )
        {
            DirectoryUtils.deleteRecursively(db);
        }

        args = new String[]
            {
                "--database.0",
                "file:testdb/testdb",
                "--dbname.0",
                "xdb"
            };
        Server.main(args);

        SqlSession session;
        try (InputStream stream = Resources.getResource("example-mybatis.xml").openStream())
        {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
            Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
            mybatisConfiguration.addMapper(AttributeEntityMapper.class);
            session = sqlSessionFactory.openSession();
        }

        AttributeEntityMapper mapper = session.getMapper(AttributeEntityMapper.class);
        mapper.createTable();

        Thread.currentThread().join();
    }
}
