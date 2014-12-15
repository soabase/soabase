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

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@CacheNamespace()   // TODO get cache attr correct
public interface AttributeEntityMapper
{
    @Select("SELECT * FROM SoaAttributes")
    public List<AttributeEntity> selectAll();

    @Update("CREATE TABLE SoaAttributes (fKEY VARCHAR(255) NOT NULL, fSCOPE VARCHAR(255) NOT NULL, fVALUE VARCHAR(65535), fTIMESTAMP VARCHAR(255) NOT NULL, PRIMARY KEY (fKEY, fSCOPE))")
    public int createTable();

    @Insert("INSERT INTO SoaAttributes (fKEY, fSCOPE, fVALUE, fTIMESTAMP) VALUES (#{fKEY}, #{fSCOPE}, #{fVALUE}, #{fTIMESTAMP})")
    public int insert(AttributeEntity attribute);

    @Update("UPDATE SoaAttributes SET fVALUE = #{fVALUE} WHERE fKEY = #{fKEY} AND fSCOPE = #{fSCOPE} AND fTIMESTAMP = #{fTIMESTAMP}")
    public int update(AttributeEntity attribute);
}
