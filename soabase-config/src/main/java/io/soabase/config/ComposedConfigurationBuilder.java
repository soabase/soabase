package io.soabase.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import java.lang.reflect.Modifier;
import java.util.Set;

public class ComposedConfigurationBuilder<T extends ComposedConfiguration>
{
    private final CtClass ctClass;
    private final ClassPool ctPool;
    private final Set<Class<?>> classes = Sets.newHashSet();

    public static final String DEFAULT_COMPOSED_FQ_CLASS_NAME = "io.soabase.core.config.SoaComposedConfiguration";

    public ComposedConfigurationBuilder(Class<T> baseClass)
    {
        this(DEFAULT_COMPOSED_FQ_CLASS_NAME, baseClass);
    }

    public ComposedConfigurationBuilder(String fqClassName, Class<T> baseClass)
    {
        ClassPool localCtPool;
        CtClass localCtClass;
        try
        {
            localCtPool = ClassPool.getDefault();
            localCtClass = localCtPool.makeClass(fqClassName);
            localCtClass.setSuperclass(localCtPool.get(baseClass.getName()));
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
        ctClass = localCtClass;
        ctPool = localCtPool;
    }

    public Class<T> build()
    {
        try
        {
            //noinspection unchecked
            Class<T> clazz = (Class<T>)ctClass.toClass();
            ctClass.detach();
            return clazz;
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    public <C> void add(String name, Class<C> clazz)
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
