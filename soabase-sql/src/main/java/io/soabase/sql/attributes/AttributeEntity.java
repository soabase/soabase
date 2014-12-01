package io.soabase.sql.attributes;

import java.util.UUID;

public class AttributeEntity
{
    private String fID;
    private String fKEY;
    private String fGROUP;
    private String fINSTANCE;
    private String fVALUE;
    private String fTIMESTAMP;

    public AttributeEntity()
    {
    }

    public AttributeEntity(String fID, String fKEY, String fGROUP, String fINSTANCE, String fVALUE)
    {
        this(fID, fKEY, fGROUP, fINSTANCE, fVALUE, UUID.randomUUID().toString());
    }

    public AttributeEntity(String fKEY, String fGROUP, String fINSTANCE, String fVALUE)
    {
        this(UUID.randomUUID().toString(), fKEY, fGROUP, fINSTANCE, fVALUE, UUID.randomUUID().toString());
    }

    public AttributeEntity(String fID, String fKEY, String fGROUP, String fINSTANCE, String fVALUE, String fTIMESTAMP)
    {
        this.fID = fID;
        this.fKEY = fKEY;
        this.fGROUP = fGROUP;
        this.fINSTANCE = fINSTANCE;
        this.fVALUE = fVALUE;
        this.fTIMESTAMP = fTIMESTAMP;
    }

    public String getfID()
    {
        return fID;
    }

    public void setfID(String fID)
    {
        this.fID = fID;
    }

    public String getfKEY()
    {
        return fKEY;
    }

    public void setfKEY(String fKEY)
    {
        this.fKEY = fKEY;
    }

    public String getfGROUP()
    {
        return fGROUP;
    }

    public void setfGROUP(String fGROUP)
    {
        this.fGROUP = fGROUP;
    }

    public String getfINSTANCE()
    {
        return fINSTANCE;
    }

    public void setfINSTANCE(String fINSTANCE)
    {
        this.fINSTANCE = fINSTANCE;
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
