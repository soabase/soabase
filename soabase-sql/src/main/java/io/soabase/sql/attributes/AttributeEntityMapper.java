package io.soabase.sql.attributes;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

public interface AttributeEntityMapper
{
    @Select("SELECT * FROM SoaBaseAttributes")
    public List<AttributeEntity> selectAll();

    @Update("CREATE TABLE SoaBaseAttributes (fID VARCHAR(255) NOT NULL, fKEY VARCHAR(255) NOT NULL, fGROUP VARCHAR(255), fINSTANCE VARCHAR(255), fVALUE VARCHAR(65535), fTIMESTAMP VARCHAR(255) NOT NULL, PRIMARY KEY (fID))")
    public int createDatabase();

    @Insert("INSERT INTO SoaBaseAttributes (fID, fKEY, fGROUP, fINSTANCE, fVALUE, fTIMESTAMP) VALUES (#{fID}, #{fKEY}, #{fGROUP}, #{fINSTANCE}, #{fVALUE}, #{fTIMESTAMP})")
    public int insert(AttributeEntity attribute);

    @Update("UPDATE SoaBaseAttributes SET fKEY = #{fKEY}, fGROUP = #{fGROUP}, fINSTANCE = #{fINSTANCE}, fVALUE = #{fVALUE} WHERE fID = #{fID} AND fTIMESTAMP = #{fTIMESTAMP}")
    public int update(AttributeEntity attribute);
}
