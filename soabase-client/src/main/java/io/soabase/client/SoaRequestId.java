package io.soabase.client;

import java.util.UUID;

public class SoaRequestId
{
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    public static final String REQUEST_ID_HEADER_NAME = "soa-request-id";

    public static String get()
    {
        return requestId.get();
    }

    static void set(String id)
    {
        requestId.set(id);
    }

    static String create()
    {
        String id = requestId.get();
        if ( id == null )
        {
            id = UUID.randomUUID().toString();
            requestId.set(id);
        }
        return id;
    }

    static void clear()
    {
        requestId.remove();
    }

    private SoaRequestId()
    {
    }
}
