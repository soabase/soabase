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
package io.soabase.core.features.client;

import javax.ws.rs.core.HttpHeaders;
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

    public static String get(HttpHeaders headers)
    {
        return headers.getHeaderString(REQUEST_ID_HEADER_NAME);
    }

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
