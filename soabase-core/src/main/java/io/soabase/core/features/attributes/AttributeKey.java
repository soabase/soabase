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

import com.google.common.base.Preconditions;

public class AttributeKey
{
    private final String key;
    private final String scope;

    public AttributeKey(String key, String scope)
    {
        this.key = Preconditions.checkNotNull(key, "key cannot be null");
        this.scope = Preconditions.checkNotNull(scope, "scope cannot be null");
    }

    public String getKey()
    {
        return key;
    }

    public String getScope()
    {
        return scope;
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

        AttributeKey that = (AttributeKey)o;

        if ( !key.equals(that.key) )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( !scope.equals(that.scope) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "AttributeKey{" +
            "key='" + key + '\'' +
            ", scope='" + scope + '\'' +
            '}';
    }
}
