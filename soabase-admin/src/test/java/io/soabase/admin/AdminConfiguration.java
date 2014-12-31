package io.soabase.admin;

import io.soabase.sql.attributes.SqlConfiguration;
import io.soabase.zookeeper.discovery.CuratorConfiguration;

public class AdminConfiguration extends SoaAdminConfiguration
{
    public CuratorConfiguration curator = new CuratorConfiguration();
    public SqlConfiguration sql = new SqlConfiguration();
}
