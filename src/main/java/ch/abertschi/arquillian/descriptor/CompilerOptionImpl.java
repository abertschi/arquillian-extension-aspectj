package ch.abertschi.arquillian.descriptor;

/**
 * Created by abertschi on 29/03/16.
 */
public class CompilerOptionImpl implements AspectjDescriptorBuilder.CompilerOption
{
    private final AspectjDescriptorBuilder mRootBuilder;

    private AspectJDescriptorModel.Compiler mModel = new AspectJDescriptorModel.Compiler();

    public CompilerOptionImpl(AspectjDescriptorBuilder rootBuilder)
    {
        mRootBuilder = rootBuilder;
    }

    @Override
    public AspectjDescriptorBuilder.CompilerOption verbose()
    {
        mModel.setVerbose(true);
        return this;
    }

    @Override
    public AspectjDescriptorBuilder set()
    {
        return mRootBuilder;
    }

    public AspectJDescriptorModel.Compiler getModel()
    {
        return mModel;
    }
}
