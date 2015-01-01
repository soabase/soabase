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
