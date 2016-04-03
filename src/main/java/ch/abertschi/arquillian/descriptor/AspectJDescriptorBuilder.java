package ch.abertschi.arquillian.descriptor;

/**
 * Created by abertschi on 07/02/16.
 */
public interface AspectjDescriptorBuilder
{
    CompilerOption compiler();

    WeavingLibraryOption weave(String name);

    String exportAsString();

    interface LibraryFilterOption<T extends LibraryFilterOption<T>>
    {
        T include(String pattern);

        T include(Package packageObject);

        T include(Class<?>... types);

        T exclude(String pattern);

        T exclude(Package packageObject);

        T exclude(Class<?>... types);
    }

    interface WeavingLibraryOption extends LibraryFilterOption<WeavingLibraryOption>
    {

        AspectLibraryOption withAspectsInWeavingLibrary();

        AspectLibraryOption withAspects(String name);

        AspectjDescriptorBuilder and();
    }

    interface AspectLibraryOption extends LibraryFilterOption<AspectLibraryOption>
    {
        AspectLibraryOption and(String name);

        WeavingLibraryOption addAspects();
    }

    interface CompilerOption
    {
        CompilerOption verbose();

        AspectjDescriptorBuilder and();
    }
}

