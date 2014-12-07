package io.soabase.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;
import io.dropwizard.Application;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Command(name = "soa", description = "Base application library for Java Service Oriented Applications")
public class SoaCli
{
    @Inject
    public HelpOption helpOption;

    @Option(name = {"-c", "--config"}, description = "Inline Dropwizard config (yaml or json)")
    public String configStr = null;

    @Option(name = {"-f", "--config-file"}, description = "Dropwizard config file (yaml or json)")
    public String configFile = null;

    @Option(name = {"-o", "--config-overrides"}, description = "List of config overrides in the form: path.to.key=value")
    public List<String> configOverrides = Lists.newArrayList();

    @Option(name = {"-d", "--dropwizard-args"}, description = "List of Dropwizard arguments. If present, you _must_ include the server command. Use " + CONFIG_SUBSTITUTION + " to have the config file location substituted.")
    public List<String> dropwizardArgs = null;

    public static final String CONFIG_SUBSTITUTION = "$CONFIG";

    public static String[] parseArgs(String[] args) throws IOException
    {
        SoaCli soaCli = SingleCommand.singleCommand(SoaCli.class).parse(args);
        if ( soaCli.helpOption.showHelpIfRequested() )
        {
            return null;
        }

        File configFile = getConfigFile(soaCli);

        if ( soaCli.dropwizardArgs == null )
        {
            if ( configFile != null )
            {
                soaCli.dropwizardArgs = Lists.newArrayList("server", configFile.getCanonicalPath());
            }
            else
            {
                soaCli.dropwizardArgs = Lists.newArrayList("server");
            }
        }
        else
        {
            int index = soaCli.dropwizardArgs.indexOf(CONFIG_SUBSTITUTION);
            if ( index >= 0 )
            {
                soaCli.dropwizardArgs.set(index, configFile.getCanonicalPath());
            }
        }

        return soaCli.dropwizardArgs.toArray(new String[soaCli.dropwizardArgs.size()]);
    }

    public static Future<Void> runAsync(final Application<?> application, final String[] args) throws Exception
    {
        ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("SoaApplication-%d").build());
        Callable<Void> callable = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                application.run(args);
                return null;
            }
        };
        return service.submit(callable);
    }

    private static File getConfigFile(SoaCli soaCli) throws IOException
    {
        boolean hasConfigFile = (soaCli.configFile != null);
        boolean hasConfigStr = (soaCli.configStr != null);
        boolean hasOverrides = (soaCli.configOverrides.size() > 0);

        if ( !hasConfigFile && !hasConfigStr && !hasOverrides )
        {
            return null;
        }

        if ( hasConfigFile && hasConfigStr )
        {
            Help.help(soaCli.helpOption.commandMetadata);
            throw new IllegalStateException("You cannot have both config string and config file");
        }

        if ( hasConfigFile && !hasOverrides )
        {
            return new File(soaCli.configFile);    // simple case - just a file
        }

        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node;
        if ( hasConfigStr )
        {
            node = mapper.readTree(yamlFactory.createParser(soaCli.configStr));
        }
        else if ( hasConfigFile )
        {
            node = mapper.readTree(yamlFactory.createParser(new File(soaCli.configFile)));
        }
        else
        {
            node = mapper.createObjectNode();
        }

        for ( String override : soaCli.configOverrides )
        {
            List<String> parts = Splitter.on('=').limit(2).trimResults().splitToList(override);
            if ( parts.size() != 2 )
            {
                Help.help(soaCli.helpOption.commandMetadata);
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
