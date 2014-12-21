package io.soabase.config;

import com.google.common.collect.Lists;
import java.util.ServiceLoader;

public class FromServices<T extends ComposedConfiguration>
{
    private String fqClassName = ComposedConfigurationBuilder.DEFAULT_COMPOSED_FQ_CLASS_NAME;
    private Class<T> baseClass;

    public static ComposedConfigurationFactoryFactory<ComposedConfiguration> standardFactory()
    {
        return FromServices.create().withBaseClass(ComposedConfiguration.class).factory();
    }

    public static ComposedConfigurationBuilder<ComposedConfiguration> standardBuilder()
    {
        return FromServices.create().withBaseClass(ComposedConfiguration.class).builder();
    }

    public static <T extends ComposedConfiguration> FromServices<T> create()
    {
        return new FromServices<>();
    }

    public FromServices<T> withFqClassName(String fqClassName)
    {
        this.fqClassName = fqClassName;
        return this;
    }

    public FromServices<T> withBaseClass(Class<T> baseClass)
    {
        this.baseClass = baseClass;
        return this;
    }

    public ComposedConfigurationBuilder<T> builder()
    {
        ComposedConfigurationBuilder<T> builder = new ComposedConfigurationBuilder<>(fqClassName, baseClass);
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
        return builder;
    }

    public ComposedConfigurationFactoryFactory<T> factory()
    {
        return new ComposedConfigurationFactoryFactory<>(builder());
    }

    private FromServices()
    {
    }
}
