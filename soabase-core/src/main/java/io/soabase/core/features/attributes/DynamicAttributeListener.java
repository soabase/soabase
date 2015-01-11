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
package io.soabase.core.features.attributes;

/**
 * Listener for dynamic attribute changes
 */
public interface DynamicAttributeListener
{
    /**
     * The given key/scope has changed
     *
     * @param key key
     * @param scope scope
     */
    public void attributeChanged(String key, String scope);

    /**
     * The given key/scope was added
     *
     * @param key key
     * @param scope scope
     */
    public void attributeAdded(String key, String scope);

    /**
     * The given key/scope was removed
     *
     * @param key key
     * @param scope scope
     */
    public void attributeRemoved(String key, String scope);

    /**
     * The given key has been overridden changed
     *
     * @param key key
     */
    public void overrideAdded(String key);

    /**
     * The override for the given key has been removed
     *
     * @param key key
     */
    public void overrideRemoved(String key);
}
