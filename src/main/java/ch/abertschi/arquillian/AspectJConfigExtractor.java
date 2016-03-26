package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectJDescriptorModel;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by abertschi on 25/03/16.
 */
public class AspectJConfigExtractor implements ApplicationArchiveProcessor
{
    private static final String CONFIG_FILE = "/META-INF/aspectj.json";

    @Override
    public void process(Archive<?> archive, TestClass testClass)
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
                parseConfiguration(json);
            }
        }
        this.exportArchive(archive);
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

    private void exportArchive(Archive<?> archive) {
        File dir = new File("./target/exploded");
        dir.mkdirs();
        archive.as(ExplodedExporter.class).exportExploded(dir);
        for (Map.Entry<ArchivePath, Node> entry: archive.getContent().entrySet()) {
            String path = entry.getKey().get();
            System.out.println(path);
        }
    }
}
