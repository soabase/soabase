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
package io.soabase.jdbi.attributes;

import com.google.common.base.Preconditions;
import io.soabase.core.SoaInfo;
import io.soabase.core.features.attributes.StandardAttributesContainer;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AttributeEntity implements Serializable
{
    private String fKEY;
    private String fSCOPE;
    private String fVALUE;
    private String fTIMESTAMP;

    public AttributeEntity()
    {
        this("", "");
    }

    public AttributeEntity(String fKEY, String fVALUE)
    {
        this(fKEY, StandardAttributesContainer.DEFAULT_SCOPE, fVALUE);
    }

    public AttributeEntity(String fKEY, String fSCOPE, String fVALUE)
    {
        this.fKEY = Preconditions.checkNotNull(fKEY, "fKEY cannot be null");
        this.fSCOPE = Preconditions.checkNotNull(fSCOPE, "fSCOPE cannot be null");
        this.fVALUE = fVALUE;
        fTIMESTAMP = UUID.randomUUID().toString();
        fTIMESTAMP = SoaInfo.newUtcFormatter().format(new Date());
    }

    public String getfKEY()
    {
        return fKEY;
    }

    public void setfKEY(String fKEY)
    {
        this.fKEY = fKEY;
    }

    public String getfSCOPE()
    {
        return fSCOPE;
    }

    public void setfSCOPE(String fSCOPE)
    {
        this.fSCOPE = fSCOPE;
    }

    public String getfVALUE()
    {
        return fVALUE;
    }

    public void setfVALUE(String fVALUE)
    {
        this.fVALUE = fVALUE;
    }

    public String getfTIMESTAMP()
    {
        return fTIMESTAMP;
    }

    public void setfTIMESTAMP(String fTIMESTAMP)
    {
        this.fTIMESTAMP = fTIMESTAMP;
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

        AttributeEntity that = (AttributeEntity)o;

        if ( !fKEY.equals(that.fKEY) )
        {
            return false;
        }
        if ( !fSCOPE.equals(that.fSCOPE) )
        {
            return false;
        }
        if ( fTIMESTAMP != null ? !fTIMESTAMP.equals(that.fTIMESTAMP) : that.fTIMESTAMP != null )
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if ( fVALUE != null ? !fVALUE.equals(that.fVALUE) : that.fVALUE != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = fKEY.hashCode();
        result = 31 * result + fSCOPE.hashCode();
        result = 31 * result + (fVALUE != null ? fVALUE.hashCode() : 0);
        result = 31 * result + (fTIMESTAMP != null ? fTIMESTAMP.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "AttributeEntity{" +
            "fKEY='" + fKEY + '\'' +
            ", fSCOPE='" + fSCOPE + '\'' +
            ", fVALUE='" + fVALUE + '\'' +
            ", fTIMESTAMP='" + fTIMESTAMP + '\'' +
            '}';
    }
}
