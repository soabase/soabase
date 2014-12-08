package io.soabase.core;

import io.dropwizard.Configuration;

public interface SoaMainPortAccessor
{
    public <T extends Configuration> int getMainPort(T configuration);
}
