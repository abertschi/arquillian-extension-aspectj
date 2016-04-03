package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.AspectLibrary;

import static ch.abertschi.arquillian.descriptor.AspectjDescriptorBuilder.*;

/**
 * Created by abertschi on 01/04/16.
 */
public class AspectLibraryBuilder extends AbstractLibraryFilterBuilder<AspectLibraryOption> implements AspectLibraryOption
{
    private WeavingLibraryOption mRootBuilder;

    public AspectLibraryBuilder(WeavingLibraryOption rootBuilder, String libraryName)
    {
        super(libraryName);
        mRootBuilder = rootBuilder;
    }

    @Override
    public AspectLibraryOption and(String name)
    {
        return mRootBuilder.withAspects(name);
    }

    @Override
    public WeavingLibraryOption addAspects()
    {
        return mRootBuilder;
    }

    public AspectLibrary build()
    {
        return new AspectLibrary(super.getName())
                .setIncludes(super.getIncludes())
                .setExcludes(super.getExcludes());
    }
}
