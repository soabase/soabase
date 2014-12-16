package io.soabase.example;

import com.google.common.io.Resources;
import io.soabase.sql.attributes.AttributeEntity;
import io.soabase.sql.attributes.AttributeEntityMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hsqldb.Server;
import java.io.InputStream;
import java.util.List;

public class MockDatabase
{
    @SuppressWarnings("ParameterCanBeLocal")
    public static void main(String[] args) throws Exception
    {
        args = new String[]
            {
                "--database.0",
                "mem:test",
                "--dbname.0",
                "xdb",
                "--port",
                "10064"
            };
        Server.main(args);

        SqlSession session;
        try (InputStream stream = Resources.getResource("example-mybatis.xml").openStream())
        {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
            Configuration mybatisConfiguration = sqlSessionFactory.getConfiguration();
            mybatisConfiguration.addMapper(AttributeEntityMapper.class);
            session = sqlSessionFactory.openSession(true);
        }

        AttributeEntityMapper mapper = session.getMapper(AttributeEntityMapper.class);
        mapper.createTable();

        AttributeEntity attribute = new AttributeEntity("test", "main");
        mapper.insert(attribute);

        List<AttributeEntity> attributeEntities = mapper.selectAll();
        System.out.println(attributeEntities);

        System.out.println("Running...");
        Thread.currentThread().join();
    }
}
