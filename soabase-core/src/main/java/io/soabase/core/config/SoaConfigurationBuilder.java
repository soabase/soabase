package io.soabase.core.config;

import com.google.common.collect.Lists;
import java.util.ServiceLoader;

public class SoaConfigurationBuilder
{
    public static ComposedConfiguration buildComposedConfiguration()
    {
        ComposedConfigurationBuilder builder = new ComposedConfigurationBuilder();
        ServiceLoader<ComposedConfigurationFactory> serviceLoader = ServiceLoader.load(ComposedConfigurationFactory.class);
        for ( ComposedConfigurationFactory factory : Lists.newArrayList(serviceLoader.iterator()) )
        {
            try
            {
                factory.addToBuilder(builder);
            }
            catch ( Exception e )
            {
                // TODO logging
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }

    private SoaConfigurationBuilder()
    {
    }
}
