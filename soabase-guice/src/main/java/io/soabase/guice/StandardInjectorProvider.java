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

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardInjectorProvider implements InjectorProvider
{
    private final Module[] modules;

    public StandardInjectorProvider(Module... modules)
    {
        this.modules = (modules != null) ? Arrays.copyOf(modules, modules.length) : new Module[0];
    }

    @Override
    public Injector get(Configuration configuration, Environment environment, Module additionalModule)
    {
        List<Module> localModules = Lists.newArrayList();
        Collections.addAll(localModules, modules);
        if ( additionalModule != null )
        {
            localModules.add(additionalModule);
        }
        return Guice.createInjector(localModules);
    }
}
