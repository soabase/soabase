package io.soabase.core.listening;

import java.util.concurrent.Executor;

/**
 * Generic holder POJO for a listener and its executor
 */
class ListenerEntry<T>
{
    final T        listener;
    final Executor executor;

    ListenerEntry(T listener, Executor executor)
    {
        this.listener = listener;
        this.executor = executor;
    }
}
