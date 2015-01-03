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

import io.dropwizard.Configuration;

/**
 * By default, Soabase determines the main port and the admin port
 * from Dropwizard's DefaultServerFactory or SimpleServerFactory. If you
 * use a different factory or want to override this behavior, have your application's
 * Configuration class implement this interface.
 */
public interface SoaMainPortAccessor<T extends Configuration>
{
    /**
     * Return the main port to use for the application
     *
     * @param configuration config
     * @return main port
     */
    public int getMainPort(T configuration);

    /**
     * Return the admin port to use for the application
     *
     * @param configuration config
     * @return admin port
     */
    public int getAdminPort(T configuration);
}
