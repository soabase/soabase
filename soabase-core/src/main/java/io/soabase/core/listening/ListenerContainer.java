package io.soabase.core.listening;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Abstracts an object that has listeners
 */
public class ListenerContainer<T> implements Listenable<T>
{
    private final Logger                        log = LoggerFactory.getLogger(getClass());
    private final Map<T, ListenerEntry<T>>      listeners = Maps.newConcurrentMap();

    @Override
    public void addListener(T listener)
    {
        addListener(listener, MoreExecutors.directExecutor());
    }

    @Override
    public void addListener(T listener, Executor executor)
    {
        listeners.put(listener, new ListenerEntry<T>(listener, executor));
    }

    @Override
    public void removeListener(T listener)
    {
        listeners.remove(listener);
    }

    /**
     * Utility - apply the given function to each listener. The function receives
     * the listener as an argument.
     *
     * @param function function to call for each listener
     */
    public void     forEach(final Function<T, Void> function)
    {
        for ( final ListenerEntry<T> entry : listeners.values() )
        {
            entry.executor.execute
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            function.apply(entry.listener);
                        }
                        catch ( Throwable e )
                        {
                            log.error(String.format("Listener (%s) threw an exception", entry.listener), e);
                        }
                    }
                }
            );
        }
    }
}
