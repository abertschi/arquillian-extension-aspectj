package ch.abertschi.arquillian;

import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import java.io.File;
import java.util.ArrayList;
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

    public static Archive<?> compileTimeWeave(List<Archive<?>> weavingLibraries, List<Archive<?>> aspectLibraries)
    {
        return weavingLibraries.get(0);
    }

    public static Archive<?> compileTimeWeave(Archive<?> weavingLibrary, List<Archive<?>> aspectLibraries)
    {
        List<Archive<?>> libs = new ArrayList<>();
        libs.add(weavingLibrary);
        return compileTimeWeave(libs, aspectLibraries);
    }

    private void exportArchive(Archive<?> archive, StringBuilder basePath)
    {
        if (basePath == null)
        {
            basePath = new StringBuilder("./target/explode/");
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
