package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.*;
import ch.abertschi.arquillian.descriptor.model.Compiler;

public class CompilerOptionBuilder implements AspectjDescriptorBuilder.CompilerOption
{
    private AspectjDescriptorBuilder mRootBuilder;
    private boolean mIsVerbose = false;

    public CompilerOptionBuilder(AspectjDescriptorBuilder rootBuilder)
    {
        mRootBuilder = rootBuilder;
    }

    @Override
    public AspectjDescriptorBuilder.CompilerOption verbose()
    {
        mIsVerbose = true;
        return this;
    }

    @Override
    public AspectjDescriptorBuilder and()
    {
        return mRootBuilder;
    }

    public boolean isVerbose()
    {
        return mIsVerbose;
    }

    public ch.abertschi.arquillian.descriptor.model.Compiler build()
    {
        Compiler c = new Compiler();
        c.setVerbose(mIsVerbose);
        return c;
    }
}
