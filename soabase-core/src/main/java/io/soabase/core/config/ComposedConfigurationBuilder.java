package io.soabase.core.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import java.lang.reflect.Method;
import java.util.Map;

public class ComposedConfigurationBuilder
{
    private final CtClass ctClass;
    private final ClassPool ctPool;
    private final Map<String, Object> values = Maps.newHashMap();

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
    public ComposedConfiguration build()
    {
        try
        {
            Class clazz = ctClass.toClass();
            Object instance = clazz.newInstance();

            for ( Map.Entry<String, Object> entry : values.entrySet() )
            {
                Method method = clazz.getMethod(setterName(entry.getKey()), entry.getValue().getClass());
                method.invoke(instance, entry.getValue());
            }

            return ComposedConfiguration.class.cast(instance);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    public <T> void add(String name, T value)
    {
        name = Preconditions.checkNotNull(name, "name cannot be null");
        value = Preconditions.checkNotNull(value, "value cannot be null");
        Preconditions.checkArgument(name.length() > 1, "Name must be at least 2 characters: " + name);

        if ( values.put(name, value) != null )
        {
            throw new RuntimeException("There is already a value set to the name: " + name);
        }

        try
        {
            CtClass fieldClass = ctPool.get(value.getClass().getName());
            CtField field = new CtField(fieldClass, name, ctClass);
            ctClass.addField(field);

            CtMethod setter = new CtMethod(CtClass.voidType, setterName(name), new CtClass[]{fieldClass}, ctClass);
            setter.setBody("this." + name + " = $1;");
            ctClass.addMethod(setter);

            CtMethod getter = new CtMethod(fieldClass, getterName(name), new CtClass[0], ctClass);
            getter.setBody("return this." + name + ";");
            ctClass.addMethod(getter);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    static String getterName(String name)
    {
        return "get" + capitalize(name);
    }

    static String setterName(String name)
    {
        return "set" + capitalize(name);
    }

    static String capitalize(String name)
    {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
