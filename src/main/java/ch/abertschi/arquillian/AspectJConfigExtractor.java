package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectJDescriptor;
import ch.abertschi.arquillian.descriptor.AspectJDescriptorModel;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.javac.comp.Enter;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import sun.net.www.content.text.Generic;
import sun.security.krb5.internal.crypto.ArcFourHmac;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by abertschi on 25/03/16.
 */
public class AspectJConfigExtractor implements ApplicationArchiveProcessor
{
    private static final String CONFIG_FILE = "/META-INF/aspectj.json";

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

    @Override
    public void process(Archive<?> archive, TestClass testClass)
    {
        AspectJDescriptorModel model = getConfigurationFromArchive(archive);
        if (model != null)
        {
            switch (ArchiveType.getTypeFromName(archive.getName()))
            {
                case EAR:
                    Map<ArchivePath, JavaArchive> jars = getJars(archive);
                    Map<ArchivePath, WebArchive> wars = getWebArchives(archive);
                    for (Map.Entry<ArchivePath, WebArchive> warEntry : wars.entrySet())
                    {
                        Map<ArchivePath, JavaArchive> warJars = getJars(warEntry.getValue());
                    }
                    break;

                case WAR:
                    break;

                case JAR:
                    List<JavaArchive> weavingLibs = new ArrayList<>();

                    break;

                default:


            }
        }
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



    private void exportArchive(Archive<?> archive)
    {
        String base = "./target/explode";
        File dir = new File(base);
        dir.mkdirs();

        archive.as(ExplodedExporter.class).exportExploded(dir);

        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            String path = entry.getKey().get();
            System.out.println(path);

            if (path.endsWith(".jar"))
            {


                JavaArchive jar = ShrinkWrap.create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream()).as(JavaArchive.class);

                File jarDir = new File(base + "/" + jar.getName());
                jarDir.mkdirs();

                jar.as(ExplodedExporter.class).exportExploded(jarDir);


                for (Map.Entry<ArchivePath, Node> jarEntry : jar.getContent().entrySet())
                {
                    String jarPath = jarEntry.getKey().get();
                    System.out.println(jarPath);
                }

            }
            else if (path.endsWith(".war"))
            {
                GenericArchive war = ShrinkWrap.create(ZipImporter.class)
                        .importFrom(entry.getValue().getAsset().openStream()).as(GenericArchive.class);

                File warDir = new File(base + "/" + war.getName());
                warDir.mkdirs();

                war.as(ExplodedExporter.class).exportExploded(warDir);

                for (Map.Entry<ArchivePath, Node> warEntry : war.getContent().entrySet())
                {
                    String warPath = warEntry.getKey().get();
                    System.out.println(warPath);

//                    /lib
//                    /lib/00c3b081-ab93-4a76-8c17-85b01598a72d.jar
//                    /6c066f73-101b-40ba-bfb0-1751f9937f08.war
//                    /WEB-INF/classes/ch
//                    /WEB-INF/lib
//                    /WEB-INF/lib/00c3b081-ab93-4a76-8c17-85b01598a72d.jar
//                    /WEB-INF/classes/ch/abertschi/arquillian/DummyGreeter.class
//                    /META-INF/beans.xml
//                    /WEB-INF/classes/ch/abertschi/arquillian
//                    /WEB-INF
//                    /META-INF
//                    /WEB-INF/classes/ch/abertschi
//                    /WEB-INF/classes


                }

            }
        }
    }
}
