package io.soabase.sql.attributes;

import com.google.common.io.Resources;
import io.dropwizard.lifecycle.Managed;
import io.soabase.core.features.attributes.SoaDynamicAttributeListener;
import io.soabase.core.features.attributes.SoaDynamicAttributes;
import io.soabase.core.features.attributes.StandardAttributesContainer;
import io.soabase.core.listening.Listenable;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

// TODO - background update
public class SqlDynamicAttributes implements SoaDynamicAttributes, Managed
{
    private final StandardAttributesContainer container;
    private final SqlSession session;

    public SqlDynamicAttributes(String mybatisConfigUrl, String groupName, String instanceName)
    {
        container = new StandardAttributesContainer(groupName, instanceName);

        try
        {
            try ( InputStream stream = Resources.getResource(mybatisConfigUrl).openStream() )
            {
                SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
                Configuration configuration = sqlSessionFactory.getConfiguration();
                configuration.addMapper(AttributeEntityMapper.class);
                session = sqlSessionFactory.openSession();
            }
        }
        catch ( IOException e )
        {
            // TODO log
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAttribute(String key)
    {
        return container.getAttribute(key, null);
    }

    @Override
    public String getAttribute(String key, String defaultValue)
    {
        return container.getAttribute(key, defaultValue);
    }

    @Override
    public boolean getAttributeBoolean(String key)
    {
        return container.getAttributeBoolean(key, false);
    }

    @Override
    public boolean getAttributeBoolean(String key, boolean defaultValue)
    {
        return container.getAttributeBoolean(key, defaultValue);
    }

    @Override
    public int getAttributeInt(String key)
    {
        return container.getAttributeInt(key, 0);
    }

    @Override
    public int getAttributeInt(String key, int defaultValue)
    {
        return container.getAttributeInt(key, defaultValue);
    }

    @Override
    public long getAttributeLong(String key)
    {
        return container.getAttributeLong(key, 0);
    }

    @Override
    public long getAttributeLong(String key, long defaultValue)
    {
        return container.getAttributeLong(key, defaultValue);
    }

    @Override
    public void temporaryOverride(String key, boolean value)
    {
        container.temporaryOverride(key, value);
    }

    @Override
    public void temporaryOverride(String key, int value)
    {
        container.temporaryOverride(key, value);
    }

    @Override
    public void temporaryOverride(String key, long value)
    {
        container.temporaryOverride(key, value);
    }

    @Override
    public void temporaryOverride(String key, String value)
    {
        container.temporaryOverride(key, value);
    }

    @Override
    public boolean removeOverride(String key)
    {
        return container.removeOverride(key);
    }

    @Override
    public Collection<String> getKeys()
    {
        return container.getKeys();
    }

    @Override
    public Listenable<SoaDynamicAttributeListener> getListenable()
    {
        return container.getListenable();
    }

    @Override
    public void start() throws Exception
    {
        update(true);
    }

    public SqlSession getSession()
    {
        return session;
    }

    @Override
    public void stop() throws Exception
    {
        if ( session != null )
        {
            session.close();
        }
    }

    private void update(boolean firstTime)
    {
        AttributeEntityMapper mapper = session.getMapper(AttributeEntityMapper.class);
        for ( AttributeEntity entity : mapper.selectAll() )
        {
            
        }
    }
}
