package ch.abertschi.arquillian;

import ch.abertschi.arquillian.aj.AjCompiler;
import ch.abertschi.arquillian.descriptor.model.AspectJDescriptorModel;
import ch.abertschi.arquillian.descriptor.model.AspectLibrary;
import ch.abertschi.arquillian.descriptor.model.WeavingLibrary;
import com.github.underscore.$;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
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

    private String matchAnyParentDirectory(String pattern)
    {
        if (!pattern.startsWith("/") && !pattern.startsWith("*"))
        {
            pattern = "**/" + pattern;
        }
        return pattern;
    }

    private String transformToMatchAnyChildDirectory(String pattern)
    {
        if (!pattern.endsWith("/") && !pattern.endsWith("*"))
        {
            pattern = pattern + "/**";
        }
        return pattern;
    }


    @Override
    public void process(Archive<?> deployableArchive, TestClass testClass)
    {
        AspectJDescriptorModel model = getConfigurationFromArchive(deployableArchive);
        if (!$.isNull(model))
        {
            AjCompiler compiler = new AjCompiler();
            for (WeavingLibrary weavingDescriptor : model.getWeaving())
            {
                List<Pair<MatchResult, Archive>> weavings = getWeavingLibraries(deployableArchive, weavingDescriptor);
                List<Archive<?>> aspects = getAspectLibraries(deployableArchive, weavingDescriptor.getAspects());

                //Archive<?> compiled = compiler.compileTimeWeave(null, aspects);


                for (Archive<?> a : aspects)
                {
                    System.out.println("Filtered archive:");
                    for (Map.Entry<ArchivePath, Node> entry : a.getContent().entrySet())
                    {
                        System.out.println(entry.getKey());

                    }

                }

            }
        }

    }




    private List<Archive<?>> getAspectLibraries(Archive<?> sourceArchive, List<AspectLibrary> aspectDescriptors)
    {
        List<Archive<?>> returns = new ArrayList<>();
        for (AspectLibrary aspectDescriptor : aspectDescriptors)
        {
            List<Archive<?>> aspects;
            if (aspectDescriptor.getName().contains(":")) // G:A:V:C
            {
                aspects = resolveWithMaven(aspectDescriptor.getName());
            }
            else
            {
                String searchPattern = this.matchAnyParentDirectory(aspectDescriptor.getName());
                aspects = $.map(searchInArchive(sourceArchive, searchPattern), matchResult -> matchResult.archive);
            }
            if ($.isEmpty(aspects))
            {
                String msg = String.format("aspect %s was neither found in deployable archive nor in maven repository", aspectDescriptor.getName());
                throw new RuntimeException(msg);
            }

            List<String> aspectIncludes = this.transformToMatchAnyChildDirectory(aspectDescriptor.getIncludes());
            List<String> aspectExcludes = this.transformToMatchAnyChildDirectory(aspectDescriptor.getExcludes());
            for (Archive<?> aspect : aspects)
            {
                returns.add(filterArchive(aspect, aspectIncludes, aspectExcludes));
            }
        }
        return Collections.unmodifiableList(returns);
    }

    private List<Pair<MatchResult, Archive>> getWeavingLibraries(Archive<?> sourceArchive, WeavingLibrary weavingDescriptor)
    {
        List<Pair<MatchResult, Archive>> returns = new ArrayList<>();

        String pattern = this.matchAnyParentDirectory(weavingDescriptor.getName());
        List<MatchResult> weavings = searchInArchive(sourceArchive, pattern);
        if (!$.isEmpty(weavings))
        {
            for (MatchResult weaving : weavings)
            {
                List<String> includes = this.transformToMatchAnyChildDirectory(weavingDescriptor.getIncludes());
                List<String> excludes = this.transformToMatchAnyChildDirectory(weavingDescriptor.getExcludes());
                Archive<?> filtered = filterArchive(weaving.archive, includes, excludes);

                returns.add(new Pair<>(weaving, filtered));
            }
        }
        else
        {
            String msg = String.format("weaving %s could not be found in deployable", weavingDescriptor.getName());
            throw new RuntimeException(msg);
        }
        return Collections.unmodifiableList(returns);
    }

    private List<Archive<?>> resolveWithMaven(String name)
    {
        List<Archive<?>> results = new ArrayList<>();
        for (JavaArchive jar : Maven.resolver().resolve(name).withTransitivity().asList(JavaArchive.class))
        {
            results.add(jar);
        }
        return results;
    }

    private Archive<?> filterArchive(Archive<?> archive, List<String> includes, List<String> excludes)
    {
        boolean hasIncludeFilter = false;
        boolean hasExcludeFilter = false;
        if (includes != null || includes.size() > 0)
        {
            hasIncludeFilter = true;
        }
        if (excludes != null || excludes.size() > 0)
        {
            hasExcludeFilter = true;
        }
        Archive<?> filteredArchive = archive.shallowCopy();
        if (hasIncludeFilter || hasExcludeFilter)
        {
            for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
            {
                boolean isFile = entry.getValue().getAsset() == null;
                if (isFile)
                {
                    if (hasIncludeFilter && !this.matchesPattern(entry.getKey().get(), includes)
                            || hasExcludeFilter && this.matchesPattern(entry.getKey().get(), excludes))
                    {
                        System.out.println("Removing from " + archive.getName() + " " + entry.getKey());
                        filteredArchive.delete(entry.getKey());
                    }
                }
            }
        }

        // 1. get corresponding aspectj lib
        // 2. compile weaving lib with aspect lib
        // merge result into weaving lib, overwrite with changes of recompilation
        return filteredArchive;
    }

    private List<String> transformToMatchAnyChildDirectory(List<String> patterns)
    {
        return Collections.unmodifiableList($.map(patterns, s -> this.transformToMatchAnyChildDirectory(s)));
    }

    private boolean matchesPattern(String path, List<String> patterns)
    {
        boolean matches = false;
        for (String pat : patterns)
        {
            if (MATCHER.match(pat, path))
            {
                matches = true;
                break;
            }
        }
        return matches;
    }

    private List<MatchResult> searchInArchive(Archive<?> archive, String pattern)
    {
        return _searchInArchive(archive, archive.getName(), pattern);
    }

    private List<MatchResult> _searchInArchive(Archive<?> archive, String basepath, String pattern)
    {
        List<MatchResult> returns = new ArrayList<>();
        if (MATCHER.match(pattern, basepath))
        {
            System.out.println("matched!");
            MatchResult result = new MatchResult();
            result.path = basepath;
            result.archive = archive;
            result.parentContainer = null;
            returns.add(result);
        }
        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            String path = basepath + entry.getKey().get().toString();
            System.out.println("comparing " + pattern + " with " + path);

            if (MATCHER.match(pattern, path))
            {
                Archive<?> subArchive = convertToArchive(entry.getValue().getAsset());
                System.out.println("matched!");
                MatchResult result = new MatchResult();
                result.path = path;
                result.archive = subArchive;
                result.parentContainer = archive;
                returns.add(result);
            }

            if (path.endsWith(".jar") || path.endsWith(".war"))
            {
                Archive<?> subArchive = convertToArchive(entry.getValue().getAsset());

                List<MatchResult> subResults = _searchInArchive(subArchive, path, pattern);
                if (subResults != null && subResults.size() > 0)
                {
                    returns.addAll(subResults);
                }
            }
        }
        return returns;
    }

    private static class MatchResult
    {
        Archive<?> parentContainer;
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
