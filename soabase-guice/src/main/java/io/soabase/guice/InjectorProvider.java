package io.soabase.guice;

import com.google.inject.Injector;
import javax.inject.Provider;

public class InjectorProvider implements Provider<Injector>
{
    private final Injector injector;

    public InjectorProvider(Injector injector)
    {
        this.injector = injector;
    }

    @Override
    public Injector get()
    {
        return injector;
    }
}
