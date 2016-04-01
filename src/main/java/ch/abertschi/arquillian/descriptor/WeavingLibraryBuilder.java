package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.AspectLibrary;
import ch.abertschi.arquillian.descriptor.model.WeavingLibrary;

import java.util.ArrayList;
import java.util.List;

import static ch.abertschi.arquillian.descriptor.AspectjDescriptorBuilder.*;

/**
 * Created by abertschi on 01/04/16.
 */
public class WeavingLibraryBuilder extends AbstractLibraryFilterBuilder<WeavingLibraryOption> implements WeavingLibraryOption
{
    private AspectjDescriptorBuilder mRootBuilder;
    private List<AspectLibraryBuilder> mAspectBuilders = new ArrayList<>();

    public WeavingLibraryBuilder(AspectjDescriptorBuilder rootBuilder, String libraryName)
    {
        super(libraryName);
        mRootBuilder = rootBuilder;
    }

    @Override
    public AspectLibraryOption withAspects(String name)
    {
        AspectLibraryBuilder builder = new AspectLibraryBuilder(this, name);
        mAspectBuilders.add(builder);
        return builder;
    }

    @Override
    public AspectjDescriptorBuilder and()
    {
        return mRootBuilder;
    }

    public WeavingLibrary build()
    {
        WeavingLibrary lib = new WeavingLibrary(super.getName());
        lib.setExcludes(super.getExcludes());
        lib.setIncludes(super.getIncludes());
        lib.setAspects(new ArrayList<AspectLibrary>());
        for (AspectLibraryBuilder aspectBuilder : mAspectBuilders)
        {
            lib.getAspects().add(aspectBuilder.build());
        }
        return lib;
    }
}
