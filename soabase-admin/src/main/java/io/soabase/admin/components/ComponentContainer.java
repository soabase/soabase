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
package io.soabase.admin.components;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class ComponentContainer<T extends ComponentId> implements Iterable<T>
{
    private final List<T> components = Lists.newCopyOnWriteArrayList();

    public void removeAll()
    {
        components.clear();
    }

    public T get(final String id)
    {
        return Iterables.find(components, new Predicate<T>()
        {
            @Override
            public boolean apply(T component)
            {
                return component.getId().equals(id);
            }
        }, null);
    }

    @Override
    public Iterator<T> iterator()
    {
        return getAll().iterator();
    }

    public List<T> getAll()
    {
        return ImmutableList.copyOf(components);
    }

    public void add(T component)
    {
        Preconditions.checkArgument(get(component.getId()) == null, "There is already a component with the id: " + component.getId());
        components.add(component);
    }

    public void addAll(List<T> tabs)
    {
        for ( T component : tabs )
        {
            add(component); // add individually so that checks occur
        }
    }
}
