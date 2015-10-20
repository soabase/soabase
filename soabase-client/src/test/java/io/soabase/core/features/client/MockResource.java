package io.soabase.core.features.client;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/test")
public class MockResource
{
    private final AtomicBoolean errorResponse;
    private final AtomicInteger counter;

    @Inject
    public MockResource(AtomicBoolean errorResponse, AtomicInteger counter)
    {
        this.errorResponse = errorResponse;
        this.counter = counter;
    }

    @GET
    public String get()
    {
        counter.incrementAndGet();
        if ( errorResponse.compareAndSet(true, false) )
        {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return "test";
    }
}
