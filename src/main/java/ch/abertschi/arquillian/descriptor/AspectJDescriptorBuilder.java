package ch.abertschi.arquillian.descriptor;

import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Created by abertschi on 07/02/16.
 */
public interface AspectjDescriptorBuilder
{
    LibraryFilterOption aspectLibrary(String name);

    CompilerOption compiler();

    LibraryFilterOption weavingLibrary(String name);

    String exportAsString();

    Asset exportAsAsset();

    interface LibraryFilterOption
    {
        LibraryFilterOption include(String pattern);

        LibraryFilterOption include(Package packageObject, boolean recursive);

        LibraryFilterOption include(Class<?>... types);

        LibraryFilterOption exclude(String pattern);

        LibraryFilterOption exclude(Package packageObject, boolean recursive);

        LibraryFilterOption exclude(Class<?>... types);

        AspectjDescriptorBuilder add();
    }

    interface CompilerOption
    {
        CompilerOption verbose();

        AspectjDescriptorBuilder set();
    }
}

