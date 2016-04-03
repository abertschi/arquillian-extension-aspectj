package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.*;
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
    private List<WeavingLibraryBuilder> mWeavingBuilders = new ArrayList<>();

    private CompilerOptionBuilder mCompilerBuilder = new CompilerOptionBuilder(this);

    AspectjDescriptor()
    {
    }

    public static AspectjDescriptorBuilder create()
    {
        return new AspectjDescriptor();
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
        AspectJDescriptorModel model = new AspectJDescriptorModel()
                .setCompiler(mCompilerBuilder.build())
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
