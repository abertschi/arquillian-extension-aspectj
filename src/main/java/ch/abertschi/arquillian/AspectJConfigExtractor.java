package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.model.AspectJDescriptorModel;
import ch.abertschi.arquillian.descriptor.model.WeavingLibrary;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by abertschi on 25/03/16.
 * proof of concept
 */
public class AspectJConfigExtractor implements ApplicationArchiveProcessor
{
    private static final String CONFIG_FILE = "/META-INF/aspectj.json";

    private final AntPathMatcher MATCHER = new AntPathMatcher();

    private Map<ArchivePath, WebArchive> getWebArchives(Archive<?> archive)
    {
        Map<ArchivePath, WebArchive> wars = new HashMap<>();
        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            if (entry.getKey().get().endsWith(".war"))
            {
                WebArchive war = ShrinkWrap
                        .create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream())
                        .as(WebArchive.class);

                wars.put(entry.getKey(), war);
            }
        }
        return wars;
    }

    private Map<ArchivePath, JavaArchive> getJars(Archive<?> archive)
    {
        Map<ArchivePath, JavaArchive> jars = new HashMap<>();
        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            if (entry.getKey().get().endsWith(".jar"))
            {
                JavaArchive jar = ShrinkWrap
                        .create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream())
                        .as(JavaArchive.class);

                jars.put(entry.getKey(), jar);
            }
        }
        return jars;
    }

    private String prefixWithAnything(String pattern)
    {
        if (!pattern.startsWith("/") && !pattern.startsWith("*"))
        {
            pattern = "**/" + pattern;
        }
        return pattern;
    }

    private String suffixWithAnything(String pattern)
    {
        if (!pattern.endsWith("/") && !pattern.endsWith("*"))
        {
            pattern = pattern + "/**";
        }
        return pattern;
    }

    @Override
    public void process(Archive<?> archive, TestClass testClass)
    {
        AspectJDescriptorModel model = getConfigurationFromArchive(archive);
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (WeavingLibrary weavingLib : model.getWeaving())
        {
            String pattern = this.prefixWithAnything(weavingLib.getName());

            MatchResult result = match(archive, archive.getName(), pathMatcher, pattern);
            if (result != null)
            {

                System.out.println("found archive:");
                for (Map.Entry<ArchivePath, Node> entry : result.archive.getContent().entrySet())
                {
                    System.out.println(entry.getKey());

                }

                boolean includeAll = false;
                boolean exclude = false;
                if (weavingLib.getIncludes() == null || weavingLib.getIncludes().size() == 0)
                {
                    includeAll = true;
                }
                if (weavingLib.getExcludes() != null || weavingLib.getExcludes().size() > 0)
                {
                    exclude = true;
                }

                Archive<?> filteredArchive = result.archive.shallowCopy();
                // filter archive
                if (!includeAll || exclude)
                {
                    for (Map.Entry<ArchivePath, Node> entry : result.archive.getContent().entrySet())
                    {
                        boolean isDirectory = entry.getValue().getAsset() == null;
                        if (!isDirectory)
                        {
                            if (!includeAll && !this.matches(entry.getKey().get(), weavingLib.getIncludes())
                                    || exclude && this.matches(entry.getKey().get(), weavingLib.getExcludes()))
                            {
                                System.out.println("Removing from " + result.archive.getName() + " " + entry.getKey());
                                filteredArchive.delete(entry.getKey());
                            }
                        }
                    }
                }
                System.out.println("Filtered archive:");
                for (Map.Entry<ArchivePath, Node> entry : filteredArchive.getContent().entrySet())
                {
                    System.out.println(entry.getKey());

                }

                // 1. get corresponding aspectj lib
                // 2. compile weaving lib with aspect lib
                // merge result into weaving lib, overwrite with changes of recompilation
            }
        }
    }

    private boolean matches(String path, List<String> patterns)
    {
        boolean matches = false;
        for (String pat : patterns)
        {
            String pattern = this.suffixWithAnything(pat);
            if (MATCHER.match(pattern, path))
            {
                matches = true;
                break;
            }
        }
        return matches;
    }

    private MatchResult match(Archive<?> archive, String basepath, final AntPathMatcher matcher, String pattern)
    {
        if (matcher.match(pattern, basepath))
        {
            System.out.println("matched!");
            MatchResult result = new MatchResult();
            result.path = basepath;
            result.archive = archive;
            return result;

        }
        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            String path = basepath + entry.getKey().get().toString();
            System.out.println("comparing " + pattern + " with " + path);

            if (matcher.match(pattern, path))
            {
                Archive<?> subArchive = ShrinkWrap.create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream()).as(GenericArchive.class);

                System.out.println("matched!");
                MatchResult result = new MatchResult();
                result.path = path;
                result.archive = subArchive;
                return result;
            }

            if (path.endsWith(".jar") || path.endsWith(".war"))
            {
                Archive<?> subArchive = ShrinkWrap.create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream()).as(GenericArchive.class);

                MatchResult subResult = match(subArchive, path, matcher, pattern);
                if (subResult != null)
                {
                    return subResult;
                }
            }
        }
        return null;
    }

    private static class MatchResult
    {
        String path;
        Archive<?> archive;
    }

    private GenericArchive convertToArchive(Asset asset)
    {
        return ShrinkWrap.create(ZipImporter.class)
                .importFrom(asset.openStream()).as(GenericArchive.class);
    }

    private AspectJDescriptorModel getConfigurationFromArchive(Archive<?> archive)
    {
        Node configNode = archive.get(CONFIG_FILE);
        if (configNode != null)
        {
            String json;
            try
            {
                json = IOUtils.toString(configNode.getAsset().openStream(), "UTF-8");
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if (json != null)
            {
                return parseConfiguration(json);
            }
        }

        return null;
    }

    private AspectJDescriptorModel parseConfiguration(String json)
    {
        AspectJDescriptorModel model = null;
        if (json != null)
        {
            ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            try
            {
                model = mapper.readValue(json, AspectJDescriptorModel.class);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error in parsing aspectj.json", e);
            }
        }
        return model;
    }


    private void exportArchive(Archive<?> archive, StringBuilder basePath)
    {
        System.out.println("Calling export with " + archive.getName());
        if (basePath == null)
        {
            basePath = new StringBuilder("./target/explode/");
        }

        File dir = new File(basePath.toString());
        dir.mkdirs();

        basePath.append(archive.getName());
        basePath.append("/");


        archive.as(ExplodedExporter.class).exportExploded(dir);

        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            String path = entry.getKey().get();
            if (path.endsWith(".jar") || path.endsWith(".war"))
            {
                Archive<?> subArchive = ShrinkWrap.create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream()).as(GenericArchive.class);

                exportArchive(subArchive, basePath);
            }
        }
    }
}
