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
package io.soabase.sql.attributes;

import com.google.common.base.Preconditions;
import io.soabase.core.features.attributes.StandardAttributesContainer;
import java.util.UUID;

public class AttributeEntity
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
}
