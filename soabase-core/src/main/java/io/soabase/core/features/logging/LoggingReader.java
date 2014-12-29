package io.soabase.core.features.logging;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import jersey.repackaged.com.google.common.collect.Sets;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

public class LoggingReader
{
    private final Collection<File> mainFiles;
    private final Collection<File> archiveDirectories;

    public LoggingReader(Collection<File> mainFiles, Collection<File> archiveDirectories)
    {
        mainFiles = Preconditions.checkNotNull(mainFiles, "mainFiles cannot be null");
        archiveDirectories = Preconditions.checkNotNull(archiveDirectories, "archiveDirectories cannot be null");

        this.mainFiles = ImmutableSet.copyOf(mainFiles);
        this.archiveDirectories = ImmutableSet.copyOf(archiveDirectories);
    }

    public List<LoggingFile> listLoggingFiles()
    {
        Set<File> usedSet = Sets.newHashSet();
        List<LoggingFile> files = Lists.newArrayList();

        for ( File f : mainFiles )
        {
            if ( f.exists() )
            {
                if ( usedSet.add(f) )
                {
                    files.add(new LoggingFile(f));
                }
            }
        }

        for ( File dir : archiveDirectories )
        {
            for ( File f : Files.fileTreeTraverser().children(dir) )
            {
                if ( usedSet.add(f) )
                {
                    files.add(new LoggingFile(f));
                }
            }
        }

        return files;
    }

    public File keyToFile(final String key)
    {
        LoggingFile foundFile = Iterables.find(listLoggingFiles(), new Predicate<LoggingFile>()
        {
            @Override
            public boolean apply(LoggingFile loggingFile)
            {
                return loggingFile.getKey().equals(key);
            }
        }, null);
        return (foundFile != null) ? foundFile.getFile() : null;
    }

    public Object keyToEntity(String key, final boolean gzipOutput)
    {
        final File file = keyToFile(key);
        if ( file == null )
        {
            return null;
        }

        if ( gzipOutput && file.getName().endsWith(".gz") )
        {
            return file;
        }

        return new StreamingOutput()
        {
            @Override
            public void write(OutputStream entityOut) throws IOException, WebApplicationException
            {
                InputStream in = null;
                try
                {
                    if ( file.getName().endsWith(".zip") )
                    {
                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                        zipInputStream.getNextEntry();
                        in = new BufferedInputStream(zipInputStream);
                    }
                    else if ( file.getName().endsWith(".gz") || file.getName().endsWith(".gzip") )
                    {
                        in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
                    }
                    else
                    {
                        in = new BufferedInputStream(new FileInputStream(file));
                    }
                    if ( gzipOutput )
                    {
                        GZIPOutputStream out = new GZIPOutputStream(entityOut);
                        ByteStreams.copy(in, out);
                        out.finish();
                    }
                    else
                    {
                        ByteStreams.copy(in, entityOut);
                    }
                }
                finally
                {
                    if ( in != null )
                    {
                        in.close();
                    }
                }
            }
        };
    }
}
