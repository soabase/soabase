package io.soabase.jdbi.attributes;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttributeEntityMapper implements ResultSetMapper<AttributeEntity>
{
    @Override
    public AttributeEntity map(int index, ResultSet resultSet, StatementContext context) throws SQLException
    {
        return new AttributeEntity(resultSet.getString("fKEY"), resultSet.getString("fSCOPE"), resultSet.getString("fVALUE"));
    }
}
