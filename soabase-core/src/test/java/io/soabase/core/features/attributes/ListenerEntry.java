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
package io.soabase.core.features.attributes;

class ListenerEntry
{
    final String event;
    final String key;
    final String scope;

    public ListenerEntry(String event, String key, String scope)
    {
        this.event = event;
        this.key = key;
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ListenerEntry listenerEntry = (ListenerEntry)o;

        if ( !event.equals(listenerEntry.event) )
        {
            return false;
        }
        if ( !key.equals(listenerEntry.key) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !scope.equals(listenerEntry.scope) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = event.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }
}
