package ch.abertschi.arquillian.descriptor;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by abertschi on 07/02/16.
 */
public class AspectjDescriptor implements AspectjDescriptorBuilder,
        AspectjDescriptorBuilder.AspectOrOtherOption,
        AspectjDescriptorBuilder.WeavingOrOtherOption,
        AspectjDescriptorBuilder.CompilerOrOtherOption
{


    public static void main(String[] args)
    {
        String json = AspectjDescriptor.create()
                .weaving()
                .within(AspectjDescriptor.class)
                .aspects()
                .addJar("ch.test:test.jar")
                .excludeAspect("ch.logging*")
                .compiler()
                .verbose()
                .exportAsString();

        System.out.println(json);
    }

    boolean mVerbose = false;
    List<String> mWeavingIncludes = new ArrayList<>();
    List<String> mWeavingExcludes = new ArrayList<>();
    List<String> mAspectIncludes = new ArrayList<>();
    List<String> mAspectExcludes = new ArrayList<>();

    private AspectjDescriptor()
    {
    }

    public static AspectjDescriptorBuilder create()
    {
        return new AspectjDescriptor();
    }

    @Override
    public AspectjDescriptorBuilder.AspectOption aspects()
    {
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.CompilerOption compiler()
    {
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.WeavingOption weaving()
    {
        return this;
    }

    @Override
    public String exportAsString()
    {
        AspectJDescriptorModel m = new AspectJDescriptorModel();
        m.aspects.includes = mAspectIncludes;
        m.aspects.excludes = mAspectExcludes;
        m.compiler.verbose = mVerbose;
        m.weaving.includes = mWeavingIncludes;
        m.weaving.excludes = mWeavingExcludes;

        ObjectMapper mapper;
        mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(m);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can not create json file", e);
        }
    }

    @Override
    public AspectOrOtherOption addJar(String name)
    {
        return this.includeAspect(name);
    }

    @Override
    public AspectOrOtherOption includeAspect(String pattern)
    {
        this.mAspectIncludes.add(pattern);
        return this;
    }

    @Override
    public AspectOrOtherOption includeAspect(Package packageObject, boolean recursive)
    {
        return this.includeAspect(this.getPackagePattern(packageObject, recursive));
    }

    @Override
    public AspectOrOtherOption excludeAspect(String pattern)
    {
        this.mAspectExcludes.add(pattern);
        return this;
    }

    @Override
    public AspectOrOtherOption excludeAspect(Package packageObject, boolean recursive)
    {
        return this.excludeAspect(this.getPackagePattern(packageObject, recursive));
    }

    @Override
    public AspectOrOtherOption excludeAspect(Class<?>... types)
    {
        Arrays.asList(types).forEach(type -> this.excludeAspect(type.getCanonicalName()));
        return this;
    }


    @Override
    public CompilerOrOtherOption verbose()
    {
        this.mVerbose = true;
        return this;
    }

    @Override
    public WeavingOrOtherOption within(String pattern)
    {
        this.mWeavingIncludes.add(pattern);
        return this;
    }

    @Override
    public WeavingOrOtherOption within(Package packageObject, boolean recursive)
    {
        return this.within(this.getPackagePattern(packageObject, recursive));
    }

    @Override
    public WeavingOrOtherOption within(Class<?>... types)
    {
        Arrays.asList(types).forEach(type -> this.within(type.getCanonicalName()));
        return this;
    }

    @Override
    public WeavingOrOtherOption exclude(String pattern)
    {
        this.mWeavingExcludes.add(pattern);
        return this;
    }

    @Override
    public WeavingOrOtherOption exclude(Package packageObject, boolean recursive)
    {
        return this.exclude(this.getPackagePattern(packageObject, recursive));
    }

    private String getPackagePattern(Package p, boolean recursive)
    {
        String name = p.getName();

        if (!name.endsWith("*") && recursive)
        {
            name = name + "*";
        }
        return name;
    }

    @Override
    public WeavingOrOtherOption exclude(Class<?>... types)
    {
        Arrays.asList(types).forEach(type -> this.exclude(type.getCanonicalName()));
        return this;
    }
}
