package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.AspectLibrary;
import com.github.underscore.$;

/**
 * Created by abertschi on 15/04/16.
 */
public class AspectBuilder extends FilterBase<AspectjDescriptorBuilder.AspectLibraryOption> implements AspectjDescriptorBuilder.AspectLibraryOption
{
    private AspectjDescriptorBuilder.WeavingLibraryOption mRootBuilder;

    private String mName;

    public AspectBuilder(AspectjDescriptorBuilder.WeavingLibraryOption rootBuilder, String name)
    {
        mName = name;
        mRootBuilder = rootBuilder;
    }

    @Override
    public AspectjDescriptorBuilder.WeavingLibraryOption addAspectLibrary()
    {
        return mRootBuilder;
    }

    public AspectLibrary build()
    {
        return new AspectLibrary(mName)
                .setIncludes($.map(super.getIncludeFilters(), filter -> filter.getFilter()))
                .setExcludes($.map(super.getExcludeFilters(), filter -> filter.getFilter()));
    }
}
