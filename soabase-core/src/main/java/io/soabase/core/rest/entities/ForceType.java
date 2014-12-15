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
package io.soabase.core.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ForceType
{
    private boolean register;

    public ForceType()
    {
    }

    public ForceType(boolean register)
    {
        this.register = register;
    }

    public boolean isRegister()
    {
        return register;
    }

    public void setRegister(boolean register)
    {
        this.register = register;
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

        ForceType forceType = (ForceType)o;

        //noinspection RedundantIfStatement
        if ( register != forceType.register )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return (register ? 1 : 0);
    }

    @Override
    public String toString()
    {
        return "ForceType{" +
            "register=" + register +
            '}';
    }
}
