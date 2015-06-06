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

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import java.util.List;

public interface AttributeEntityDao
{
    @SqlQuery("SELECT * FROM SoaAttributes")
    @Mapper(AttributeEntityMapper.class)
    public List<AttributeEntity> selectAll();

    @SqlUpdate("CREATE TABLE SoaAttributes (fKEY VARCHAR(255) NOT NULL, fSCOPE VARCHAR(255) NOT NULL, fVALUE VARCHAR(65535), fTIMESTAMP VARCHAR(255) NOT NULL, PRIMARY KEY (fKEY, fSCOPE))")
    public void createTable();

    @SqlUpdate("INSERT INTO SoaAttributes (fKEY, fSCOPE, fVALUE, fTIMESTAMP) VALUES (:fKEY, :fSCOPE, :fVALUE, :fTIMESTAMP)")
    public void insert(@Bind("fKEY") String key, @Bind("fSCOPE") String scope, @Bind("fVALUE") String value, @Bind("fTIMESTAMP") String timestamp);

    @SqlUpdate("UPDATE SoaAttributes SET fVALUE = :fVALUE WHERE fKEY = :fKEY AND fSCOPE = :fSCOPE")
    public void update(@Bind("fKEY") String key, @Bind("fSCOPE") String scope, @Bind("fVALUE") String value);

    @SqlUpdate("DELETE FROM SoaAttributes WHERE fKEY = :fKEY AND fSCOPE = :fSCOPE")
    public void delete(@Bind("fKEY") String key, @Bind("fSCOPE") String scope);
}

