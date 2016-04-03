package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.model.AspectJDescriptorModel;
import ch.abertschi.arquillian.descriptor.model.AspectLibrary;
import ch.abertschi.arquillian.descriptor.model.WeavingLibrary;
import ch.abertschi.arquillian.util.MatcherUtils;
import com.github.underscore.$;
import org.apache.commons.io.IOUtils;
import ch.abertschi.arquillian.util.ResolveUtil;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.javatuples.Pair;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by abertschi on 25/03/16.
 */
public class ArchiveProcessor implements ApplicationArchiveProcessor
{
    private static final String CONFIG_FILE = "/META-INF/aspectj.json";

    @Override
    public void process(Archive<?> deployableArchive, TestClass testClass)
    {
        AspectJDescriptorModel model = getConfigurationFromArchive(deployableArchive);
        if (!$.isNull(model))
        {
            AjCompiler compiler = new AjCompiler();
            for (WeavingLibrary weavingDescriptor : model.getWeaving())
            {
                List<Pair<ArchiveSearch.ArchiveSearchResult, Archive>> weavings = getWeavingLibraries(deployableArchive, weavingDescriptor);
                List<Archive<?>> aspects = getAspectLibraries(deployableArchive, weavingDescriptor.getAspects());

                for (Pair<ArchiveSearch.ArchiveSearchResult, Archive> weave : weavings)
                {
                    Archive<?> compiled = compiler.compileTimeWeave(weave.getValue1(), aspects);
                    Archive<?> replace = weave.getValue0().getArchive().merge(compiled);

                    // merge all aspect libraries into recompiled aspect so they are available for sure
                    for (Archive<?> aspect : aspects)
                    {
                        replace = replace.merge(aspect);
                    }
                    deployableArchive = ArchiveSearch.replaceArchive(deployableArchive, weave.getValue0().getPath(), replace);
                }
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
            if (aspectDescriptor.getName().contains(":")) // G:A:V
            {
                aspects = ResolveUtil.get().resolveWithMaven(aspectDescriptor.getName());
            }
            else
            {
                String searchPattern = MatcherUtils.transformToMatchAnyParent(aspectDescriptor.getName());
                aspects = $.map(ArchiveSearch.searchInArchive(sourceArchive, searchPattern), matchResult -> matchResult.getArchive());
            }
            if ($.isEmpty(aspects))
            {
                String msg = String.format("aspect %s was neither found in deployable archive nor in maven repository", aspectDescriptor.getName());
                throw new RuntimeException(msg);
            }
            List<String> aspectIncludes = MatcherUtils.transformToMatchAnyChild(aspectDescriptor.getIncludes());
            List<String> aspectExcludes = MatcherUtils.transformToMatchAnyChild(aspectDescriptor.getExcludes());
            for (Archive<?> aspect : aspects)
            {
                returns.add(ArchiveSearch.filterArchive(aspect, aspectIncludes, aspectExcludes));
            }
        }
        return Collections.unmodifiableList(returns);
    }

    private List<Pair<ArchiveSearch.ArchiveSearchResult, Archive>> getWeavingLibraries(Archive<?> sourceArchive, WeavingLibrary weavingDescriptor)
    {
        List<Pair<ArchiveSearch.ArchiveSearchResult, Archive>> returns = new ArrayList<>();
        String pattern = MatcherUtils.transformToMatchAnyParent(weavingDescriptor.getName());
        List<ArchiveSearch.ArchiveSearchResult> weavings = ArchiveSearch.searchInArchive(sourceArchive, pattern);

        if (!$.isEmpty(weavings))
        {
            for (ArchiveSearch.ArchiveSearchResult weaving : weavings)
            {
                List<String> includes = MatcherUtils.transformToMatchAnyChild(weavingDescriptor.getIncludes());
                List<String> excludes = MatcherUtils.transformToMatchAnyChild(weavingDescriptor.getExcludes());

                Archive<?> filtered = ArchiveSearch.filterArchive(weaving.getArchive(), includes, excludes);
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

    private AspectJDescriptorModel getConfigurationFromArchive(Archive<?> archive)
    {
        Node configNode = archive.get(CONFIG_FILE);
        if (configNode != null)
        {
            String json;
            try
            {
                json = IOUtils.toString(configNode.getAsset().openStream(), "UTF-8");
                System.out.println(json);
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
}
