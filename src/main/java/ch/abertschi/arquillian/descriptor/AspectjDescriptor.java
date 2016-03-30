package ch.abertschi.arquillian.descriptor;

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
public class AspectJDescriptor implements AspectjDescriptorBuilder {

    private List<LibraryFilterOptionImpl> mWeavingLibs = new ArrayList<>();
    private List<LibraryFilterOptionImpl> mAspectLibs = new ArrayList<>();
    private CompilerOptionImpl mCompilerOptions = new CompilerOptionImpl(this);

    public static AspectjDescriptorBuilder create()
    {
        return new AspectJDescriptor();
    }

    public static void main(String[] args)
    {
        String json = AspectJDescriptor.create()
                .aspects()
                .classes()
                .library("")
                .
                .include(AspectJDescriptor.class)
                .include(AspectJDescriptorModel.class.getPackage(), true)
                .exclude(String.class)
                .add()
                .weavingLibrary("classes-to-weave.jar")
                .include(AspectJDescriptor.class)
                .add()
                .compiler()
                .verbose()
                .set()
                .exportAsString();

        System.out.println(json);
    }

    @Override
    public LibraryFilterOption aspectLibrary(String name)
    {
        LibraryFilterOptionImpl lib = new LibraryFilterOptionImpl(this, name);
        mAspectLibs.add(lib);
        return lib;
    }

    @Override
    public CompilerOption compiler()
    {
        return mCompilerOptions;
    }

    @Override
    public LibraryFilterOption weavingLibrary(String name)
    {
        LibraryFilterOptionImpl lib = new LibraryFilterOptionImpl(this, name);
        mWeavingLibs.add(lib);
        return lib;
    }

    @Override
    public String exportAsString()
    {
        ObjectMapper mapper;
        mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        AspectJDescriptorModel model = new AspectJDescriptorModel();
        model.setCompiler(mCompilerOptions.getModel());

        for (LibraryFilterOptionImpl lib : mWeavingLibs)
        {
            model.getWeaving().add(lib.getModel());
        }
        for (LibraryFilterOptionImpl lib : mAspectLibs)
        {
            model.getAspects().add(lib.getModel());
        }
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can not create json file", e);
        }
    }

    @Override
    public Asset exportAsAsset()
    {
        return new StringAsset(this.exportAsString());
    }
}
