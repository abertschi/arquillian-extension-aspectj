package ch.abertschi.arquillian;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abertschi on 27/04/16.
 */
public class Cache
{
    private static final String TEMP_DIR = "java.io.tmpdir";
    private static final String CACHE_DIR = "arquillian-aspectj-extention-cache";
    private static final String CACHE_INDEX = "cache-index.xml";
    private static final XStream XSTREAM = new XStream();

    private File cacheHomeDir;

    private Cache(File cacheFile)
    {
        this.cacheHomeDir = cacheFile;
    }

    public static Cache create(File location)
    {
        return new Cache(location);
    }

    public static Cache createWithDefaults()
    {
        String tmpDir = System.getProperty(TEMP_DIR);
        File cacheDir = new File(tmpDir, CACHE_DIR);
        cacheDir.mkdirs();
        return new Cache(cacheDir);
    }

    public File getCacheBaseDir()
    {
        return this.cacheHomeDir;
    }

    private File getCacheIndexFile()
    {
        return new File(this.cacheHomeDir, CACHE_INDEX);
    }

    public String getChecksum(Archive<?> archive)
    {
        try
        {
            return Integer.toString(IOUtils.toByteArray(archive.as(ZipExporter.class).exportAsInputStream()).length);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can not create checksum of archive " + archive.getName(), e);
        }
    }

    public Archive<?> getFromCache(Archive<?> keyArchive)
    {
        String checksum = getChecksum(keyArchive);
        Map<String, String> storage = loadCacheIndex();
        String cached = storage.get(checksum);
        if (cached == null)
        {
            return null;
        }
        else
        {
            File cachedFile = new File(cached);
            if (!cachedFile.exists())
            {
                return null;
            }
            return ShrinkWrap.create(ZipImporter.class)
                    .importFrom(cachedFile)
                    .as(GenericArchive.class);
        }
    }

    public void storeInCache(Archive<?> keyArchive, Archive<?> archiveToCache)
    {
        String checksum = getChecksum(keyArchive);
        File persist = new File(getCacheBaseDir(), checksum + "-" + archiveToCache.getName());
        archiveToCache.as(ZipExporter.class).exportTo(persist, true);
        addToCacheIndex(checksum, persist.getAbsolutePath());
    }

    protected Map<String, String> loadCacheIndex()
    {
        Map<String, String> cacheIndex = new HashMap<>();
        File cacheIndexFile = getCacheIndexFile();
        if (cacheIndexFile.exists())
        {
            cacheIndex = (Map<String, String>) XSTREAM.fromXML(cacheIndexFile);
        }
        return cacheIndex;
    }

    protected void addToCacheIndex(String key, String value)
    {
        Map<String, String> cacheIndex = loadCacheIndex();
        cacheIndex.put(key, value);

        File cacheIndexFile = getCacheIndexFile();
        FileOutputStream out;
        try
        {
            out = new FileOutputStream(cacheIndexFile);
            XSTREAM.toXML(cacheIndex, out);
            out.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
