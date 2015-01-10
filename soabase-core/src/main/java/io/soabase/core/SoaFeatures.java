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
package io.soabase.core;

import io.soabase.core.features.ExecutorBuilder;
import io.soabase.core.features.attributes.DynamicAttributes;
import io.soabase.core.features.discovery.SoaDiscovery;
import java.util.Collection;

/**
 * Container for various Soabase singletons
 */
public interface SoaFeatures
{
    /**
     * Default name to use for {@link #getNamed(Class, String)}
     */
    public static final String DEFAULT_NAME = "default";

    /**
     * Name for the admin JerseyEnvironment
     */
    public static final String ADMIN_NAME = "soa-admin";

    /**
     * Return the stored named object
     *
     * @param clazz object's class
     * @param name name of the object
     * @return the object or null
     */
    public <T> T getNamed(Class<T> clazz, String name);

    /**
     * Return the set of names registered for the given class
     * @param clazz class to check
     * @return set of names
     */
    public <T> Collection<String> getNames(Class<T> clazz);

    /**
     * Return the stored named object or throws {@link NullPointerException}
     *
     * @param clazz object's class
     * @param name name of the object
     * @return the object (never null)
     * @throws NullPointerException if an object of the given class with the given name isn't registered
     */
    public <T> T getNamedRequired(Class<T> clazz, String name);

    /**
     * Registers an object with a name. The name must be unique for the given class. i.e. there can
     * only be 1 object with a given name and class
     *
     * @param o the object
     * @param clazz the object's class
     * @param name name to store
     */
    public <T> void putNamed(T o, Class<T> clazz, String name);

    /**
     * Return the service discovery instance
     *
     * @return service discovery instance
     */
    public SoaDiscovery getDiscovery();

    /**
     * Return the dynamic attributes instance
     *
     * @return dynamic attributes instance
     */
    public DynamicAttributes getAttributes();

    /**
     * Returns information about this instance
     *
     * @return info
     */
    public SoaInfo getSoaInfo();

    /**
     * Access to the Dropwizard executor builders from the Dropwizard environment
     *
     * @return Dropwizard executor builders
     */
    public ExecutorBuilder getExecutorBuilder();
}
