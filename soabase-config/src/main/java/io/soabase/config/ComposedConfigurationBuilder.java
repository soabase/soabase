package io.soabase.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import java.lang.reflect.Modifier;
import java.util.Set;

public class ComposedConfigurationBuilder
{
    private final CtClass ctClass;
    private final ClassPool ctPool;
    private final Set<Class<?>> classes = Sets.newHashSet();

    public ComposedConfigurationBuilder()
    {
        this("io.soabase.core.config.SoaComposedConfiguration");
    }

    public ComposedConfigurationBuilder(String fqClassName)
    {
        ClassPool localCtPool;
        CtClass localCtClass;
        try
        {
            localCtPool = ClassPool.getDefault();
            localCtClass = localCtPool.makeClass(fqClassName);
            localCtClass.setSuperclass(localCtPool.get(ComposedConfiguration.class.getName()));
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
        ctClass = localCtClass;
        ctPool = localCtPool;
    }

    @SuppressWarnings("unchecked")
    public Class<ComposedConfiguration> build()
    {
        try
        {
            return ctClass.toClass();
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    public <T> void add(String name, Class<T> clazz)
    {
        name = Preconditions.checkNotNull(name, "name cannot be null");
        clazz = Preconditions.checkNotNull(clazz, "clazz cannot be null");
        Preconditions.checkArgument(name.length() > 0, "Name cannot be empty: " + name);
        Preconditions.checkArgument(classes.add(clazz), "There is already a field of type: " + clazz);

        try
        {
            CtClass fieldClass = ctPool.get(clazz.getName());
            CtField field = new CtField(fieldClass, name, ctClass);
            field.setModifiers(Modifier.PUBLIC);
            ctClass.addField(field, "new " + clazz.getName() + "()");
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }
}
