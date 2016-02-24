package ch.abertschi.arquillian;

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
        WeavingOrOtherOption within(String pattern, boolean recursive);

        WeavingOrOtherOption within(String pattern);

        WeavingOrOtherOption within(Package packageObject, boolean recursive);

        WeavingOrOtherOption within(Package packageObject);

        WeavingOrOtherOption within(Class<?>... types);
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

        AspectOrOtherOption addAspect(String pattern);

        AspectOrOtherOption addAspect(String pattern, boolean recursive);

        AspectOrOtherOption addAspect(Package packageName, boolean recursive);

        AspectOrOtherOption addAspect(Package packageName);
    }

    interface CompilerOption
    {
        CompilerOrOtherOption verbose();
    }


}

