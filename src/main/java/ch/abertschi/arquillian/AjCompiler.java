package ch.abertschi.arquillian;

import ch.abertschi.arquillian.util.ResolverUtil;
import com.github.underscore.$;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by abertschi on 01/04/16.
 */
public class AjCompiler
{
    private static final Logger LOG = LoggerFactory.getLogger(AjCompiler.class);

    private static final String AJRT = "org.aspectj:aspectjrt:1.8.9";

    public AjCompiler()
    {
    }

    protected List<String> getInitialOptions()
    {
        List<String> options = new ArrayList<>();
        options.add("-Xlint:ignore");
        options.add("-showWeaveInfo");

        return options;
    }

    public List<Archive<?>> getRuntimeLibraries()
    {
        return (List<Archive<?>>) ((Object) ResolverUtil.get().resolve(AJRT).withTransitivity().asList(JavaArchive.class));
    }

    public Archive<?> compileTimeWeave(List<Archive<?>> weavingLibraries, List<Archive<?>> aspectLibraries)
    {
        List<String> options = getInitialOptions();
        File base = new File(new File("."), "target/arquillian-aspectj");
        base.mkdirs();

        File weavingWork = new File(base, "weaving-out-" + System.currentTimeMillis());
        weavingWork.mkdirs();
        options.add("-d");
        options.add(weavingWork.getAbsolutePath());

        //options.add("-outjar");
        //options.add(weavedFile.getAbsolutePath());

        File inpathDir = new File(base, "inpath");
        base.mkdirs();
        List<File> inpathJars = exportAsZip(weavingLibraries, inpathDir);
        String inpathArgs = $.join($.map(inpathJars, file -> file.getAbsolutePath()), File.pathSeparator);

        options.add("-inpath");
        options.add(inpathArgs);

        File aspectBase = new File(base, "aspectlibs");

        List<Archive<?>> aspectWithRuntime = new ArrayList<>();
        aspectWithRuntime.addAll(aspectLibraries);
        aspectWithRuntime.addAll(getRuntimeLibraries());

        List<File> aspectFiles = exportAsZip(aspectWithRuntime, aspectBase);
        String aspects = $.join($.map(aspectFiles, file -> file.getAbsolutePath()), File.pathSeparator);

        options.add("-aspectpath");
        options.add(aspects);

        LOG.debug("Calling aj compiler with " + $.join(options, " "));
        Main compiler = new Main();
        MessageHandler m = new MessageHandler();
        compiler.run(options.toArray(new String[options.size()]), m);
        IMessage[] ms = m.getMessages(null, true);
        $.forEach(Arrays.asList(ms), o -> LOG.debug(o.getMessage()));

        return importFromDir(weavingWork);
    }

    public Archive<?> compileTimeWeave(Archive<?> weavingLibrary, List<Archive<?>> aspectLibraries)
    {
        List<Archive<?>> libs = new ArrayList<>();
        libs.add(weavingLibrary);
        return compileTimeWeave(libs, aspectLibraries);
    }

    public Archive<?> compileTimeWeave(Archive<?> weavingLibrary, Archive<?> aspectLib)
    {
        List<Archive<?>> libs = new ArrayList<>();
        libs.add(aspectLib);
        return compileTimeWeave(weavingLibrary, libs);
    }

    private JavaArchive importFromDir(File where)
    {
        return ShrinkWrap.create(ExplodedImporter.class)
                .importDirectory(where)
                .as(org.jboss.shrinkwrap.api.spec.JavaArchive.class);
    }

    private File exportAsZip(Archive<?> archive, File basepath)
    {
        File f = new File(basepath, archive.getName());
        basepath.mkdirs();
        archive.as(ZipExporter.class).exportTo(f, true);
        return f;
    }

    private List<File> exportAsZip(List<Archive<?>> archives, File basepath)
    {
        return $.map(archives, archive -> exportAsZip(archive, basepath));
    }
}
