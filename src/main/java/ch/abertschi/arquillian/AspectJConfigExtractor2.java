//package ch.abertschi.arquillian;
//
//import ch.abertschi.arquillian.descriptor.AspectJDescriptorModel;
//import org.apache.commons.io.IOUtils;
//import org.codehaus.jackson.annotate.JsonAutoDetect;
//import org.codehaus.jackson.annotate.JsonMethod;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
//import org.jboss.arquillian.test.spi.TestClass;
//import org.jboss.shrinkwrap.api.*;
//import org.jboss.shrinkwrap.api.asset.Asset;
//import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
//import org.jboss.shrinkwrap.api.importer.ZipImporter;
//import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.jboss.shrinkwrap.resolver.api.maven.Maven;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by abertschi on 25/03/16.
// */
//public class AspectJConfigExtractor2 implements ApplicationArchiveProcessor
//{
//    private static final String CONFIG_FILE = "/META-INF/aspectj.json";
//
//    @Override
//    public void process(Archive<?> archive, TestClass testClass)
//    {
//        AspectJDescriptorModel model = getConfigurationFromArchive(archive);
//
//
//        if (model != null)
//        {
//
//            switch (ArchiveType.getTypeFromName(archive.getName()))
//            {
//                case EAR:
//                    EnterpriseArchive ear = (EnterpriseArchive) archive;
//                    break;
//
//                case WAR:
//                    break;
//
//                case JAR:
//                    List<JavaArchive> weavingLibs = new ArrayList<>();
//                    for (String lib : model.getWeaving().getLibraries())
//                    {
//                        if (archive.getName().equals(lib))
//                        {
//                            weavingLibs.add((JavaArchive) archive);
//
//                        }
//                        else
//                        {
//                            JavaArchive jar = Maven.configureResolver()
//                                    .workOffline()
//                                    .loadPomFromFile("pom.xml")
//                                    .resolve()
//                                    .withoutTransitivity().asSingle(JavaArchive.class);
//                            weavingLibs.add(jar);
//                        }
//                    }
//
//                    List<JavaArchive> aspectLibs = new ArrayList<>();
//                    for (String lib : model.getAspects().getLibraries())
//                    {
//                        if (archive.getName().equals(lib))
//                        {
//                            aspectLibs.add((JavaArchive) archive);
//
//                        }
//                        else
//                        {
//                            JavaArchive jar = Maven.configureResolver()
//                                    .workOffline()
//                                    .loadPomFromFile("pom.xml")
//                                    .resolve()
//                                    .withoutTransitivity().asSingle(JavaArchive.class);
//                            aspectLibs.add(jar);
//                        }
//                    }
//
//                    break;
//
//                default:
//
//
//            }
//
//
////        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
////        {
////            String path = entry.getKey().get();
////            System.out.println(path);
////            if (path.endsWith(".jar") || path.endsWith(".ejb"))
////            {
////
////            }
////            else if (path.endsWith(".war"))
////            {
////                System.out.println(entry.getValue());
////                entry.getValue()
////                for(Node n: entry.getValue().getChildren()) {
////                    System.out.println(n.getPath().get());
////                }
////
////
////            }
////        }
//
//
//        }
//
//
//        // 1.) check if aspectj libs included
//        //   - add transient dependencies for aspectj libs
//        //   - if aspect definition not yet compiled, create aspect jar
//        // 2.) check if weaving libs included
//        //
//
//
//        this.exportArchive(archive);
//    }
//
////    private List<String> getFilteredArchiveNodes(Archive<?> archive, List<String> includes, List<String> excludes)
////    {
////        final boolean defaultInclude = includes == null || includes.size() == 0;
////        final boolean defaultExclude = excludes == null || excludes.size() == 0;
////
////        Map<ArchivePath, Node> filtered = archive.getContent(new Filter<ArchivePath>()
////        {
////            @Override
////            public boolean include(ArchivePath object)
////            {
////                if (defaultInclude && defaultExclude)
////                {
////                    return true;
////                }
////                else if (defaultInclude)
////                {
////
////                }
////            }
////        });
////
////        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
////        {
////            String path = entry.getKey().get();
////            if (path.endsWith(".class"))
////            {
////
////            }
////        }
////    }
//
//    private GenericArchive convertToArchive(Asset asset)
//    {
//        return ShrinkWrap.create(ZipImporter.class)
//                .importFrom(asset.openStream()).as(GenericArchive.class);
//    }
//
//    private AspectJDescriptorModel getConfigurationFromArchive(Archive<?> archive)
//    {
//        Node configNode = archive.get(CONFIG_FILE);
//        if (configNode != null)
//        {
//            String json;
//            try
//            {
//                json = IOUtils.toString(configNode.getAsset().openStream(), "UTF-8");
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//            if (json != null)
//            {
//                return parseConfiguration(json);
//            }
//        }
//
//        return null;
//    }
//
//    private AspectJDescriptorModel parseConfiguration(String json)
//    {
//        AspectJDescriptorModel model = null;
//        if (json != null)
//        {
//            ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//            try
//            {
//                model = mapper.readValue(json, AspectJDescriptorModel.class);
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException("Error in parsing aspectj.json", e);
//            }
//        }
//        return model;
//    }
//
//    private void exportArchive(Archive<?> archive)
//    {
//        String base = "./target/explode";
//        File dir = new File(base);
//        dir.mkdirs();
//
//        archive.as(ExplodedExporter.class).exportExploded(dir);
//
//        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
//        {
//            String path = entry.getKey().get();
//            System.out.println(path);
//
//            if (path.endsWith(".jar"))
//            {
//
//
//                JavaArchive jar = ShrinkWrap.create(ZipImporter.class)
//                        .importFrom(entry.getValue().getAsset().openStream()).as(JavaArchive.class);
//
//                File jarDir = new File(base + "/" + jar.getName());
//                jarDir.mkdirs();
//
//                jar.as(ExplodedExporter.class).exportExploded(jarDir);
//
//
//                for (Map.Entry<ArchivePath, Node> jarEntry : jar.getContent().entrySet())
//                {
//                    String jarPath = jarEntry.getKey().get();
//                    System.out.println(jarPath);
//                }
//
//            }
//            else if (path.endsWith(".war"))
//            {
//                GenericArchive war = ShrinkWrap.create(ZipImporter.class)
//                        .importFrom(entry.getValue().getAsset().openStream()).as(GenericArchive.class);
//
//                File warDir = new File(base + "/" + war.getName());
//                warDir.mkdirs();
//
//                war.as(ExplodedExporter.class).exportExploded(warDir);
//
//                for (Map.Entry<ArchivePath, Node> warEntry : war.getContent().entrySet())
//                {
//                    String warPath = warEntry.getKey().get();
//                    System.out.println(warPath);
//
////                    /lib
////                    /lib/00c3b081-ab93-4a76-8c17-85b01598a72d.jar
////                    /6c066f73-101b-40ba-bfb0-1751f9937f08.war
////                    /WEB-INF/classes/ch
////                    /WEB-INF/lib
////                    /WEB-INF/lib/00c3b081-ab93-4a76-8c17-85b01598a72d.jar
////                    /WEB-INF/classes/ch/abertschi/arquillian/DummyGreeter.class
////                    /META-INF/beans.xml
////                    /WEB-INF/classes/ch/abertschi/arquillian
////                    /WEB-INF
////                    /META-INF
////                    /WEB-INF/classes/ch/abertschi
////                    /WEB-INF/classes
//
//
//                }
//
//            }
//        }
//    }
//}
