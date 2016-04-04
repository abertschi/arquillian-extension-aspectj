package ch.abertschi.arquillian;

import com.github.underscore.$;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by abertschi on 01/04/16.
 */
public class AjCompiler
{
    public AjCompiler()
    {

    }

    public Archive<?> compileTimeWeave(List<Archive<?>> weavingLibraries, List<Archive<?>> aspectLibraries)
    {
        List<String> options = new ArrayList<>();

        File base = new File(new File("."), "./target/aj");
        options.add("-d");
        options.add(base.getAbsolutePath());

        options.add("-outjar");
        options.add("weaved.jar");

        File weavingBase = new File(new File("."), "./target/ajc");
        List<File> inPaths = exportAsZip(weavingLibraries, weavingBase);
        String inPath = $.join($.map(inPaths, file -> file.getAbsolutePath()), File.pathSeparator);

        options.add("-inpath");
        options.add(inPath);

        File aspectBase = new File(new File("."), "./target/aspects");
        List<File> aspectFiles = exportAsZip(aspectLibraries, aspectBase);
        String aspects = $.join($.map(aspectFiles, file -> file.getAbsolutePath()), File.pathSeparator);

        options.add("-aspectpath");
        options.add(aspects);

        System.out.println($.join(options, " "));

        Main compiler = new Main();
        MessageHandler m = new MessageHandler();
        compiler.run(options.toArray(new String[options.size()]), m);
        IMessage[] ms = m.getMessages(null, true);
        System.out.println("messages: " + Arrays.asList(ms));

        return weavingLibraries.get(0);
    }

    public Archive<?> compileTimeWeave(Archive<?> weavingLibrary, List<Archive<?>> aspectLibraries)
    {
        List<Archive<?>> libs = new ArrayList<>();
        libs.add(weavingLibrary);
        return compileTimeWeave(libs, aspectLibraries);
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

    private void exportArchive(Archive<?> archive, StringBuilder basePath)
    {
        if (basePath == null)
        {
            basePath = new StringBuilder("./target/ajc/");
        }
        File dir = new File(basePath.toString());
        dir.mkdirs();

        basePath.append(archive.getName()).append("/");
        archive.as(ExplodedExporter.class).exportExploded(dir);

        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            String path = entry.getKey().get();
            if (path.endsWith(".jar") || path.endsWith(".war"))
            {
                Archive<?> subArchive = ShrinkWrap.create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream())
                        .as(GenericArchive.class);
                exportArchive(subArchive, basePath);
            }
        }
    }
}
