package io.soabase.admin;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;
import javax.inject.Inject;

@Command(name = "Soabase Admin Console", description = "Extensible administration console for Soabase applications")
public class SoaAdminOptions
{
    @Inject
    public HelpOption helpOption;

    @Option(name = {"--name"}, description = "Display name for the Application")
    public String appName = "Soabase";

    public static SoaAdminOptions get(String... args)
    {
        SoaAdminOptions options = SingleCommand.singleCommand(SoaAdminOptions.class).parse(args);
        if ( options.helpOption.showHelpIfRequested() )
        {
            return null;
        }
        return options;
    }
}
