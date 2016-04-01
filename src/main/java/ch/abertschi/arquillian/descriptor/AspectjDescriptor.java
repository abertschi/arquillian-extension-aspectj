package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.*;
import ch.abertschi.arquillian.descriptor.model.Compiler;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 07/02/16.
 */
public class AspectJDescriptor implements AspectjDescriptorBuilder
{

    private List<WeavingLibraryBuilder> mWeavingBuilders = new ArrayList<>();
    private CompilerOptionBuilder mCompilerBuilder = new CompilerOptionBuilder(this);

    public static AspectjDescriptorBuilder create()
    {
        return new AspectJDescriptor();
    }

    public static void main(String[] args)
    {
        String json = AspectJDescriptor
                .create()
                .weave("webarchive.war")
                .include("/WEB-INF/classes")
                .exclude("/WEB-INF/classes/ch/abertschi/debug")
                .withAspects("ch.abertschi:mytest")
                .and("ch.abertschi:mytest2")
                .addAspects()
                .and()
                .weave("webarchive.war/**/jar-to-weave-*.jar")
                .include("/ch/abertschi")
                .exclude("**test")
                .withAspects("ch.abertschi:mytest")
                .exclude(CompilerOption.class)
                .addAspects()
                .and()
                .compiler()
                .verbose()
                .and()
                .exportAsString();

        System.out.println(json);
    }


    @Override
    public CompilerOption compiler()
    {
        return mCompilerBuilder;
    }

    @Override
    public WeavingLibraryOption weave(String name)
    {
        WeavingLibraryBuilder builder = new WeavingLibraryBuilder(this, name);
        mWeavingBuilders.add(builder);
        return builder;
    }

    @Override
    public String exportAsString()
    {
        ObjectMapper mapper;
        mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        AspectJDescriptorModel model = new AspectJDescriptorModel();
        model.setCompiler(mCompilerBuilder.build());
        List<WeavingLibrary> weavingLibs = new ArrayList<>();
        for (WeavingLibraryBuilder builder : mWeavingBuilders)
        {
            weavingLibs.add(builder.build());
        }
        model.setWeaving(weavingLibs);
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can not create json file", e);
        }
    }
}
