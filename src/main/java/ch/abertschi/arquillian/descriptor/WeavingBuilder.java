package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.descriptor.model.WeavingLibrary;
import ch.abertschi.arquillian.descriptor.AspectjDescriptorBuilder.WeavingLibraryOptionInitial;
import com.github.underscore.$;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 15/04/16.
 */
public class WeavingBuilder extends FilterBase<WeavingLibraryOptionInitial> implements WeavingLibraryOptionInitial
{
    private final AspectjDescriptorBuilder mRootBuilder;

    private String mName = "";

    private boolean mUseCache = false;

    private boolean mWeaveEverything = false;

    private List<AspectBuilder> mAspectBuilders = new ArrayList<>();

    public WeavingBuilder(AspectjDescriptorBuilder rootBuilder, String name)
    {
        mRootBuilder = rootBuilder;
        mName = name;
    }

    public WeavingBuilder(AspectjDescriptorBuilder rootBuilder)
    {
        mRootBuilder = rootBuilder;
        mWeaveEverything = true;
    }

    @Override
    public AspectjDescriptorBuilder.AspectLibraryOption aspectLibrary(String name)
    {
        AspectBuilder builder = new AspectBuilder(this, name);
        mAspectBuilders.add(builder);
        return builder;
    }

    @Override
    public AspectjDescriptorBuilder addWeaveDependency()
    {
        return mRootBuilder;
    }

    public WeavingLibrary build()
    {
        return new WeavingLibrary(mName)
                .setWeaveEverything(mWeaveEverything)
                .setUseCache(mUseCache)
                .setIncludes($.map(super.getIncludeFilters(), filter -> filter.getFilter()))
                .setExcludes($.map(super.getExcludeFilters(), filter -> filter.getFilter()))
                .setAspects($.map(mAspectBuilders, aspectBuilder -> aspectBuilder.build()));
    }

    @Override
    public WeavingLibraryOptionInitial useCache()
    {
        mUseCache = true;
        return this;
    }
}
