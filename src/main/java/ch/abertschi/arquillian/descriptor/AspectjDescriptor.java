package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.AspectjDescriptorModel;
import com.github.underscore.$;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 07/02/16.
 */
public class AspectjDescriptor implements AspectjDescriptorBuilder
{
    public static void main(String[] args)
    {
        String json = AspectjDescriptor.create()
                .weave()
                .filter(Filters.exclude(AspectjDescriptorBuilder.class))
                .filter(Filters.include("**/*test*"))
                .aspectLibrary("test:test:1.1.1")
                .filter(Filters.include(AspectjDescriptor.class.getPackage()))
                .addAspectLibrary()
                .addWeaveDependency()
                .exportAsString();

        System.out.println(json);
    }

    private List<WeavingBuilder> mWeavingBuilders = new ArrayList<>();

    AspectjDescriptor()
    {
    }

    public static AspectjDescriptorBuilder create()
    {
        return new AspectjDescriptor();
    }

    @Override
    public WeavingLibraryOptionInitial weave(String name)
    {
        WeavingBuilder builder = new WeavingBuilder(this, name);
        mWeavingBuilders.add(builder);
        return builder;
    }

    @Override
    public WeavingLibraryOptionInitial weave()
    {
        WeavingBuilder builder = new WeavingBuilder(this);
        mWeavingBuilders.add(builder);
        return builder;
    }

    @Override
    public String exportAsString()
    {
        AspectjDescriptorModel model = new AspectjDescriptorModel()
                .setWeaving($.map(mWeavingBuilders, builder -> builder.build()));
        try
        {
            return new ObjectMapper()
                    .setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY)
                    .writerWithDefaultPrettyPrinter().writeValueAsString(model);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can not create json file", e);
        }

    }
}
