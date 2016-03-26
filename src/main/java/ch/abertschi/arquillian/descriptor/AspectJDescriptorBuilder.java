package ch.abertschi.arquillian.descriptor;

/**
 * Created by abertschi on 07/02/16.
 */
interface AspectjDescriptorBuilder
{
    AspectOption aspects();

    CompilerOption compiler();

    WeavingOption weaving();

    String exportAsString();

    interface WeavingOption
    {
        WeavingOrOtherOption within(String pattern);

        WeavingOrOtherOption within(Package packageObject, boolean recursive);

        WeavingOrOtherOption within(Class<?>... types);

        WeavingOrOtherOption exclude(String pattern);

        WeavingOrOtherOption exclude(Package packageObject, boolean recursive);

        WeavingOrOtherOption exclude(Class<?>... types);
    }

    interface WeavingOrOtherOption extends AspectjDescriptorBuilder, WeavingOption
    {
    }

    interface AspectOrOtherOption extends AspectjDescriptorBuilder, AspectOption
    {
    }

    interface CompilerOrOtherOption extends AspectjDescriptorBuilder, CompilerOption
    {
    }

    interface AspectOption
    {
        AspectOrOtherOption addJar(String name);

        AspectOrOtherOption includeAspect(String pattern);

        AspectOrOtherOption includeAspect(Package packageName, boolean recursive);

        AspectOrOtherOption excludeAspect(String pattern);

        AspectOrOtherOption excludeAspect(Package packageObject, boolean recursive);

        AspectOrOtherOption excludeAspect(Class<?>... types);
    }

    interface CompilerOption
    {
        CompilerOrOtherOption verbose();
    }
}

