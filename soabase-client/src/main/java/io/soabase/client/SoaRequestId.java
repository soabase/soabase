package io.soabase.client;

import java.util.UUID;

public class SoaRequestId
{
    private static final ThreadLocal<Entry> requestId = new ThreadLocal<>();

    private static class Entry
    {
        final String id;
        final boolean isNew;

        public Entry(String id, boolean isNew)
        {
            this.id = id;
            this.isNew = isNew;
        }
    }

    public static final String REQUEST_ID_HEADER_NAME = "soa-request-id";
    public static final String REQUEST_ID_IS_NEW_HEADER_NAME = "soa-request-id-is-new";

    public static String get()
    {
        Entry entry = requestId.get();
        return (entry != null) ? entry.id : null;
    }

    public interface HeaderSetter<T>
    {
        public void setHeader(T request, String header, String value);
    }

    public static <T> void checkSetHeaders(T request, HeaderSetter<T> setter)
    {
        Entry entry = requestId.get();
        if ( entry != null )
        {
            setter.setHeader(request, REQUEST_ID_HEADER_NAME, entry.id);
            setter.setHeader(request, REQUEST_ID_IS_NEW_HEADER_NAME, Boolean.toString(entry.isNew));
        }
    }

    static void set(String id)
    {
        requestId.set(new Entry(id, false));
    }

    static String create()
    {
        Entry entry = requestId.get();
        if ( entry == null )
        {
            entry = new Entry(UUID.randomUUID().toString(), true);
            requestId.set(entry);
        }
        return entry.id;
    }

    static void clear()
    {
        requestId.remove();
    }

    private SoaRequestId()
    {
    }
}
