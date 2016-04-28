package ch.abertschi.arquillian.descriptor;

import java.util.List;

/**
 * Created by abertschi on 15/04/16.
 */
public interface AspectjDescriptorBuilder
{
    WeavingLibraryOptionInitial weave(String name);

    WeavingLibraryOptionInitial weave();

    String exportAsString();

    interface WeavingLibraryOptionInitial extends WeavingLibraryOption, FilterOption<WeavingLibraryOptionInitial>
    {
        WeavingLibraryOptionInitial useCache();
    }

    interface WeavingLibraryOption
    {
        AspectLibraryOption aspectLibrary(String name);

        AspectjDescriptorBuilder addWeaveDependency();
    }

    interface AspectLibraryOption extends FilterOption<AspectLibraryOption>
    {
//        AspectLibraryOption includeTransitiveDependencies();

        WeavingLibraryOption addAspectLibrary();
    }

    interface FilterOption<RETURN_CLASS>
    {

        RETURN_CLASS filter(Filter... filters);

        RETURN_CLASS filter(List<Filter> filters);

        RETURN_CLASS filter(Filter.FilterType type, String... patterns);
    }
}
