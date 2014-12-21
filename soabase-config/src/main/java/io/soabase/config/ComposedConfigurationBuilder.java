/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * <p>
 *     Composed configuration class generator
 * </p>
 *
 * <p>
 *     Builds a new class that extends {@link ComposedConfiguration} having
 *     fields for additional configurations. Usage: allocate a builder,
 *     {@link #add(String, Class)} configuration fields as needed and call {@link #build()} to
 *     generate the class. The class will extend {@link ComposedConfiguration}
 *     or another class if you desire. It will have named fields for each
 *     configuration that you add. Use {@link ComposedConfiguration#as(Class)} to
 *     access the configuration instances.
 * </p>
 */
public class ComposedConfigurationBuilder<T extends ComposedConfiguration>
{
    private final CtClass ctClass;
    private final ClassPool ctPool;
    private final Set<String> types = Sets.newHashSet();

    /**
     * The default fully qualified class name. IMPORTANT: each class generated
     * by ComposedConfigurationBuilder must have a unique FQCN
     */
    public static final String DEFAULT_COMPOSED_FQ_CLASS_NAME = "io.soabase.config.GeneratedComposedConfiguration";

    /**
     * Create a new builder that creates a configuration class that extends {@link ComposedConfiguration} using the
     * default FQCN. NOTE: this can only be used once within a JVM instance
     *
     * @return new builder
     */
    public static ComposedConfigurationBuilder<ComposedConfiguration> standard()
    {
        return new ComposedConfigurationBuilder<>(ComposedConfiguration.class);
    }

    /**
     * Create a new builder that creates a configuration class that extends the given base class using the default FQCN.
     * NOTE: this can only be used once within a JVM instance.
     *
     * @param baseClass the base class for the configuration class
     */
    public ComposedConfigurationBuilder(Class<T> baseClass)
    {
        this(DEFAULT_COMPOSED_FQ_CLASS_NAME, baseClass);
    }

    /**
     * Create a new builder that creates a configuration class that extends the given base class using the given FQCN.
     * NOTE: each FQCN must be unique within a JVM instance.
     *
     * @param fqClassName fully qualified class name to create
     * @param baseClass the base class for the configuration class
     */
    public ComposedConfigurationBuilder(String fqClassName, Class<T> baseClass)
    {
        fqClassName = Preconditions.checkNotNull(fqClassName, "fqClassName cannot be null");
        baseClass = Preconditions.checkNotNull(baseClass, "baseClass cannot be null");

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

    /**
     * Build and return the class
     *
     * @return new configuration class
     */
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

    /**
     * Add a configuration field of the given type to the configuration class
     *
     * @param name name of the field - must be unique in the class
     * @param type type of the field. The class MUST have a public no-arg constructor.
     */
    public <C> void add(String name, Class<C> type)
    {
        name = Preconditions.checkNotNull(name, "name cannot be null");
        type = Preconditions.checkNotNull(type, "type cannot be null");
        Preconditions.checkArgument(isJavaIdentifier(name), "Name must be a legal Java identifier: " + name);
        Preconditions.checkArgument(types.add(type.getSimpleName()), "There is already a field of type: " + type.getSimpleName());

        try
        {
            CtClass fieldClass = ctPool.get(type.getName());
            CtField field = new CtField(fieldClass, name, ctClass);
            field.setModifiers(Modifier.PUBLIC);
            ctClass.addField(field, "new " + type.getName() + "()");

            CtMethod method = new CtMethod(fieldClass, getterName(type), null, ctClass);
            method.setBody("{return this." + name + ";}");
            ctClass.addMethod(method);
        }
        catch ( Exception e )
        {
            // TODO logging
            throw new RuntimeException(e);
        }
    }

    static <C> String getterName(Class<C> clazz)
    {
        return "get" + clazz.getSimpleName();
    }

    private boolean isJavaIdentifier(String name)
    {
        boolean isFirst = true;
        for ( char c : name.toCharArray() )
        {
            if ( isFirst )
            {
                isFirst = false;
                if ( !Character.isJavaIdentifierStart(c) )
                {
                    return false;
                }
            }
            else
            {
                if ( !Character.isJavaIdentifierPart(c) )
                {
                    return false;
                }
            }
        }
        return !isFirst;
    }
}
