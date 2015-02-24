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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import javax.inject.Provider;
import java.util.Arrays;

public class InjectorProvider implements Provider<Injector>
{
    private final Injector injector;
    private final Module[] modules;

    public InjectorProvider(Injector injector)
    {
        this.injector = injector;
        modules = null;
    }

    public InjectorProvider(Module... modules)
    {
        injector = null;
        this.modules = Arrays.copyOf(modules, modules.length);
    }

    @Override
    public Injector get()
    {
        if ( modules != null )
        {
            return Guice.createInjector(modules);
        }
        return injector;
    }
}
