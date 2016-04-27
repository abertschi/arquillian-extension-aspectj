package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.model.AspectjDescriptorModel;
import ch.abertschi.arquillian.descriptor.model.AspectLibrary;
import ch.abertschi.arquillian.descriptor.model.WeavingLibrary;
import ch.abertschi.arquillian.util.MatcherUtils;
import com.github.underscore.$;
import org.apache.commons.io.IOUtils;
import ch.abertschi.arquillian.util.ResolverUtil;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.javatuples.Pair;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by abertschi on 25/03/16.
 */
public class ArchiveProcessor implements ApplicationArchiveProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveProcessor.class);

    private static final String CONFIG_FILE = "/META-INF/aspectj.json";

    private static final AjCompiler COMPILER = new AjCompiler();

    @Override
    public void process(Archive<?> deployableArchive, TestClass testClass)
    {
        AspectjDescriptorModel model = getConfigurationFromArchive(deployableArchive);
        if (!$.isNull(model))
        {
            for (WeavingLibrary weavingDescriptor : model.getWeaving())
            {
                List<Pair<ArchiveSearch.ArchiveSearchResult, Archive>> weavings = getWeavingLibraries(deployableArchive, weavingDescriptor);
                List<Archive<?>> aspects = getAspectLibraries(deployableArchive, weavingDescriptor.getAspects());

                for (Pair<ArchiveSearch.ArchiveSearchResult, Archive> weave : weavings)
                {
                    Archive<?> compiled = compile(weave.getValue1(), aspects, weavingDescriptor);
                    $.forEach(compiled.getContent().entrySet(), archivePathNodeEntry -> LOG.trace("Compiled node: " + archivePathNodeEntry.getKey()));

                    Archive<?> replace = weave.getValue0().getArchive().merge(compiled);

                    // merge all aspect libraries into recompiled aspect so they are available for sure
                    for (Archive<?> aspect : $.concat(COMPILER.getRuntimeLibraries(), aspects))
                    {
                        replace = replace.merge(aspect);
                    }
                    ArchiveSearch.replaceArchive(deployableArchive, weave.getValue0().getPath(), replace);
                }
            }
        }
    }

    private Archive<?> compile(Archive<?> source, List<Archive<?>> aspects, WeavingLibrary descriptor)
    {
        if (descriptor.isUseCache())
        {
            Archive<?> cached = Cache.getFromCache(source);
            if (cached == null)
            {
                LOG.info(String.format("Recompiling weaving library %s", source.getName()));
                Archive<?> compiled = COMPILER.compileTimeWeave(source, aspects);
                Cache.storeInCache(source, compiled);
                return compiled;
            }
            else
            {
                LOG.info(String.format("Skipping aspectj compilation. Using cached archive %s from %s", source.getName(), Cache.getCacheBaseDir()));
                return cached;
            }
        }
        else
        {
            LOG.info(String.format("Recompiling weaving library %s", source.getName()));
            return COMPILER.compileTimeWeave(source, aspects);
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
                aspects = (List<Archive<?>>) ((Object) ResolverUtil
                        .get().resolve(aspectDescriptor.getName()).withTransitivity().asList(JavaArchive.class));
            }
            else
            {
                String searchPattern = MatcherUtils.transformToMatchAnyParent(aspectDescriptor.getName());
                aspects = $.map(ArchiveSearch.searchInArchive(sourceArchive, searchPattern, false), matchResult -> matchResult.getArchive());
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
        $.forEach(returns, archive -> LOG.info(String.format("Found aspect library %s", archive.getName())));
        return Collections.unmodifiableList(returns);
    }

    private List<Pair<ArchiveSearch.ArchiveSearchResult, Archive>> getWeavingLibraries(Archive<?> sourceArchive, WeavingLibrary weavingDescriptor)
    {
        String pattern;
        if (weavingDescriptor.isWeaveEverything())
        {
            pattern = sourceArchive.getName();
        }
        else
        {
            pattern = MatcherUtils.transformToMatchAnyParent(weavingDescriptor.getName());
        }
        List<Pair<ArchiveSearch.ArchiveSearchResult, Archive>> returns = new ArrayList<>();
        List<ArchiveSearch.ArchiveSearchResult> weavings = ArchiveSearch.searchInArchive(sourceArchive, pattern, false);

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
            String msg = String.format("Weaving %s could not be found in deployable", weavingDescriptor.getName());
            throw new RuntimeException(msg);
        }

        $.forEach(returns, archive -> LOG.info(String.format("Found weaving library %s", archive.getValue0().getPath())));
        return Collections.unmodifiableList(returns);
    }

    private AspectjDescriptorModel getConfigurationFromArchive(Archive<?> archive)
    {
        Node configNode = archive.get(CONFIG_FILE);
        if (configNode != null)
        {
            String json;
            try
            {
                json = IOUtils.toString(configNode.getAsset().openStream(), "UTF-8");
                LOG.debug(json);
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

    private AspectjDescriptorModel parseConfiguration(String json)
    {
        AspectjDescriptorModel model = null;
        if (json != null)
        {
            ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
            try
            {
                model = mapper.readValue(json, AspectjDescriptorModel.class);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error in parsing aspectj.json", e);
            }
        }
        return model;
    }

    private void addAsLibraryOrMerge(Archive<?> base, List<Archive> toAdd)
    {
        if (base instanceof LibraryContainer)
        {
            ((LibraryContainer) base).addAsLibraries(toAdd);
        }
        else
        {
            $.forEach(toAdd, archive -> base.merge(archive));
        }
    }
}
