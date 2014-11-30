package io.soabase.core;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.List;

@Command(name = "soabase", description = "Base application library for Java Service Oriented Applications")
public class SoaBaseMain
{
    @Inject
    public HelpOption helpOption;

    @Option(name = {"-c", "--config"}, description = "Inline Dropwizard config (yaml or json)")
    public String configStr = null;

    @Option(name = {"-f", "--config-file"}, description = "Dropwizard config file (yaml or json)")
    public String configFile = null;

    @Option(name = {"-d", "--dropwizard-args"}, description = "List of dropwizard arguments")
    public List<String> dropwizardArgs = null;

    public static <C extends SoaBaseConfiguration, A extends SoaBaseApplication<C>> void run(Class<A> applicationClass, String[] args) throws Exception
    {
        SoaBaseMain soaBaseMain = SingleCommand.singleCommand(SoaBaseMain.class).parse(args);
        if ( soaBaseMain.helpOption.showHelpIfRequested() )
        {
            return;
        }

        boolean hasConfigStr = (soaBaseMain.configStr != null);
        boolean hasConfigFile = (soaBaseMain.configFile != null);
        if ( hasConfigFile && hasConfigStr )
        {
            System.err.println("You can have either --config or --config-file but not both");
            Help.help(soaBaseMain.helpOption.commandMetadata);
            return;
        }

        File configFile = getConfigFile(soaBaseMain, hasConfigFile, hasConfigStr);
        if ( soaBaseMain.dropwizardArgs == null )
        {
            if ( configFile != null )
            {
                soaBaseMain.dropwizardArgs = Lists.newArrayList("server", configFile.getCanonicalPath());
            }
            else
            {
                soaBaseMain.dropwizardArgs = Lists.newArrayList("server");
            }
        }

        SoaBaseApplication<C> application = applicationClass.newInstance();
        application.run(soaBaseMain.dropwizardArgs.toArray(new String[soaBaseMain.dropwizardArgs.size()]));
    }

    private static File getConfigFile(SoaBaseMain soaBaseMain, boolean hasConfigFile, boolean hasConfigStr) throws IOException
    {
        if ( hasConfigFile )
        {
            return new File(soaBaseMain.configFile);
        }

        if ( hasConfigStr )
        {
            File configFile = File.createTempFile("soabase-", ".config");
            configFile.deleteOnExit();
            Files.write(soaBaseMain.configStr, configFile, Charset.defaultCharset());
            return configFile;
        }

        return null;
    }
}
