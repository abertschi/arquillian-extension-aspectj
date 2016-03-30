package ch.abertschi.arquillian.descriptor;

/**
 * Created by abertschi on 29/03/16.
 */
public class LibraryFilterOptionImpl implements AspectjDescriptorBuilder.LibraryFilterOption
{
    private AspectJDescriptorModel.Library mModel = new AspectJDescriptorModel.Library();

    private AspectjDescriptorBuilder mRootBuilder;

    public LibraryFilterOptionImpl(AspectjDescriptorBuilder rootBuilder, String libraryName) {
        mModel.setName(libraryName);
        mRootBuilder = rootBuilder;
    }

    @Override
    public AspectjDescriptorBuilder.LibraryFilterOption include(String pattern)
    {
        mModel.getIncludes().add(pattern);
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.LibraryFilterOption include(Package packageObject, boolean recursive)
    {
        this.include(this.getPackagePattern(packageObject, recursive));
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.LibraryFilterOption include(Class<?>... types)
    {
        for (Class<?> type : types)
        {
            this.include(type.getCanonicalName());
        }
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.LibraryFilterOption exclude(String pattern)
    {
        mModel.getExcludes().add(pattern);
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.LibraryFilterOption exclude(Package packageObject, boolean recursive)
    {
        this.exclude(this.getPackagePattern(packageObject, recursive));
        return this;
    }

    @Override
    public AspectjDescriptorBuilder.LibraryFilterOption exclude(Class<?>... types)
    {
        for (Class<?> type : types)
        {
            this.exclude(type.getCanonicalName());
        }
        return this;
    }

    @Override
    public AspectjDescriptorBuilder add()
    {
        return mRootBuilder;
    }

    // -----------------------------------------
    // helper section
    // -----------------------------------------

    private String getPackagePattern(Package p, boolean recursive)
    {
        String name = p.getName();

        if (!name.endsWith("**") && recursive)
        {
            name = name + "**";
        }
        return name;
    }

    public AspectJDescriptorModel.Library getModel() {
        return mModel;
    }


}
