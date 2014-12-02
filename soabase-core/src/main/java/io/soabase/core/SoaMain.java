package io.soabase.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Command(name = "soa", description = "Base application library for Java Service Oriented Applications")
public class SoaMain
{
    @Inject
    public HelpOption helpOption;

    @Option(name = {"-c", "--config"}, description = "Inline Dropwizard config (yaml or json)")
    public String configStr = null;

    @Option(name = {"-f", "--config-file"}, description = "Dropwizard config file (yaml or json)")
    public String configFile = null;

    @Option(name = {"-o", "--config-overrides"}, optionEndsValues = true, arity = Integer.MAX_VALUE, description = "List of config overrides in the form: path.to.key=value")
    public List<String> configOverrides = Lists.newArrayList();

    @Option(name = {"-d", "--dropwizard-args"}, optionEndsValues = true, arity = Integer.MAX_VALUE, description = "List of Dropwizard arguments")
    public List<String> dropwizardArgs = null;

    public static <C extends SoaConfiguration, A extends SoaApplication<C>> SoaApplication<C> runAsync(Class<A> applicationClass, String[] args) throws Exception
    {
        BuiltApplication<C> builtApplication = buildApplication(applicationClass, args);
        builtApplication.application.runAsync(builtApplication.arguments);
        return builtApplication.application;
    }

    public static <C extends SoaConfiguration, A extends SoaApplication<C>> void run(Class<A> applicationClass, String[] args) throws Exception
    {
        BuiltApplication<C> builtApplication = buildApplication(applicationClass, args);
        builtApplication.application.run(builtApplication.arguments);
    }

    private static class BuiltApplication<C extends SoaConfiguration>
    {
        final SoaApplication<C> application;
        final String[] arguments;

        public BuiltApplication(SoaApplication<C> application, SoaMain soaMain)
        {
            this.application = application;
            this.arguments = soaMain.dropwizardArgs.toArray(new String[soaMain.dropwizardArgs.size()]);
        }
    }

    private static <C extends SoaConfiguration, A extends SoaApplication<C>> BuiltApplication<C> buildApplication(Class<A> applicationClass, String[] args) throws Exception
    {
        SoaMain soaMain = SingleCommand.singleCommand(SoaMain.class).parse(args);
        if ( soaMain.helpOption.showHelpIfRequested() )
        {
            return null;
        }

        File configFile = getConfigFile(soaMain);

        if ( soaMain.dropwizardArgs == null )
        {
            if ( configFile != null )
            {
                soaMain.dropwizardArgs = Lists.newArrayList("server", configFile.getCanonicalPath());
            }
            else
            {
                soaMain.dropwizardArgs = Lists.newArrayList("server");
            }
        }

        return new BuiltApplication<>(applicationClass.newInstance(), soaMain);
    }

    private static File getConfigFile(SoaMain soaMain) throws IOException
    {
        boolean hasConfigFile = (soaMain.configFile != null);
        boolean hasConfigStr = (soaMain.configStr != null);
        boolean hasOverrides = (soaMain.configOverrides.size() > 0);

        if ( !hasConfigFile && !hasConfigStr && !hasOverrides )
        {
            return null;
        }

        if ( hasConfigFile && hasConfigStr )
        {
            Help.help(soaMain.helpOption.commandMetadata);
            throw new IllegalStateException("You cannot have both config string and config file");
        }

        if ( hasConfigFile && !hasOverrides )
        {
            return new File(soaMain.configFile);    // simple case - just a file
        }

        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node;
        if ( hasConfigStr )
        {
            node = mapper.readTree(yamlFactory.createParser(soaMain.configStr));
        }
        else if ( hasConfigFile )
        {
            node = mapper.readTree(yamlFactory.createParser(new File(soaMain.configFile)));
        }
        else
        {
            node = mapper.createObjectNode();
        }

        for ( String override : soaMain.configOverrides )
        {
            List<String> parts = Splitter.on('=').limit(2).trimResults().splitToList(override);
            if ( parts.size() != 2 )
            {
                Help.help(soaMain.helpOption.commandMetadata);
                throw new IllegalArgumentException("Badly formed config override: " + override);
            }

            ObjectNode work = node;
            List<String> fieldList = Splitter.on('.').trimResults().splitToList(parts.get(0));
            for ( int i = 0; i < fieldList.size(); ++i )
            {
                String field = fieldList.get(i);
                boolean isLast = (i + 1) == fieldList.size();
                if ( isLast )
                {
                    work.put(field, parts.get(1));
                }
                else
                {
                    ObjectNode n = (ObjectNode)work.get(field); // I can't find a way around the cast -- too bad
                    if ( n == null )
                    {
                        n = mapper.createObjectNode();
                        work.set(field, n);
                    }
                    work = n;
                }
            }
        }

        File configFile = File.createTempFile("soa-", ".config");
        configFile.deleteOnExit();
        mapper.writeValue(configFile, node);

        return configFile;
    }
}
