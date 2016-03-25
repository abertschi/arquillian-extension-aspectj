package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.AspectjDescriptorBuilder;
import com.sun.tools.internal.xjc.outline.Aspect;

/**
 * Created by abertschi on 07/02/16.
 */
public class AspectjDescriptor implements AspectjDescriptorBuilder,
        AspectjDescriptorBuilder.AspectOrOtherOption,
        AspectjDescriptorBuilder.WeavingOrOtherOption,
        AspectjDescriptorBuilder.CompilerOption
{
    static {
        AspectjDescriptor.create()
                .weaving().within(AspectjDescriptor.class)
                .aspects().addJar("ch.test:test.jar")
                .compiler().verbose().exportAsString();
    }

    boolean mVerbose;


    private AspectjDescriptor() {
        throw new UnsupportedOperationException("I ain't for direct initiation!");
    }

    public static AspectjDescriptorBuilder create() {
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
        return null;
    }

    @Override
    public AspectOrOtherOption addJar(String name)
    {
        return null;
    }

    @Override
    public AspectOrOtherOption addAspect(String pattern)
    {
        return null;
    }

    @Override
    public AspectOrOtherOption addAspect(String pattern, boolean recursive)
    {
        return null;
    }

    @Override
    public AspectOrOtherOption addAspect(Package packageName, boolean recursive)
    {
        return null;
    }

    @Override
    public AspectOrOtherOption addAspect(Package packageName)
    {
        return null;
    }

    @Override
    public CompilerOrOtherOption verbose()
    {
        return null;
    }

    @Override
    public WeavingOrOtherOption within(String pattern, boolean recursive)
    {
        return null;
    }

    @Override
    public WeavingOrOtherOption within(String pattern)
    {
        return null;
    }

    @Override
    public WeavingOrOtherOption within(Package packageObject, boolean recursive)
    {
        return null;
    }

    @Override
    public WeavingOrOtherOption within(Package packageObject)
    {
        return null;
    }

    @Override
    public WeavingOrOtherOption within(Class<?>... types)
    {
        return null;
    }
}
