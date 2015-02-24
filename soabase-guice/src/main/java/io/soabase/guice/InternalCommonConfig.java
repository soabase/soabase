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
package io.soabase.guice;

import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.model.internal.ComponentBag;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import java.util.Map;
import java.util.Set;

class InternalCommonConfig implements Configurable<Configurable>
{
    private final CommonConfig config = new CommonConfig(RuntimeType.SERVER, ComponentBag.INCLUDE_ALL);

    @Override
    public Configuration getConfiguration()
    {
        return config.getConfiguration();
    }

    @Override
    public Configurable<Configurable> property(String name, Object value)
    {
        config.property(name, value);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Class<?> componentClass)
    {
        config.register(componentClass);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Class<?> componentClass, int priority)
    {
        config.register(componentClass, priority);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Class<?> componentClass, Class<?>... contracts)
    {
        config.register(componentClass, contracts);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
    {
        config.register(componentClass, contracts);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Object component)
    {
        config.register(component);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Object component, int priority)
    {
        config.register(component, priority);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Object component, Class<?>... contracts)
    {
        config.register(component, contracts);
        return this;
    }

    @Override
    public Configurable<Configurable> register(Object component, Map<Class<?>, Integer> contracts)
    {
        config.register(component, contracts);
        return this;
    }

    Set<Class<?>> getClasses()
    {
        return config.getClasses();
    }

    Set<Object> getInstances()
    {
        return config.getInstances();
    }

    Map<String, Object> getProperties()
    {
        return config.getProperties();
    }
}
