package io.soabase.guice.mocks;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class MockResource
{
    private final MockGuiceInjected guiceInjected;
    private final MockHK2Injected hk2Injected;

    @Inject
    public MockResource(MockGuiceInjected guiceInjected, MockHK2Injected hk2Injected)
    {
        this.guiceInjected = guiceInjected;
        this.hk2Injected = hk2Injected;
    }

    @GET
    public String get()
    {
        return guiceInjected.getValue() + " - " + hk2Injected.getValue();
    }
}
