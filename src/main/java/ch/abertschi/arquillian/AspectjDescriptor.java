package ch.abertschi.arquillian;

/**
 * Created by abertschi on 07/02/16.
 */
public class AspectjDescriptor implements AspectjDescriptorBuilder
{

    static {
        AspectjDescriptor.create()
                .weaving().within(AspectjDescriptor.class)
                .aspects().addJar("ch.test:test.jar")
                .compiler().verbose().exportAsString();
    }

    public static AspectjDescriptorBuilder create() {
        return new AspectjDescriptor();
    }

    @Override
    public AspectjDescriptorBuilder.AspectOption aspects()
    {
        return null;
    }

    @Override
    public AspectjDescriptorBuilder.CompilerOption compiler()
    {
        return null;
    }

    @Override
    public AspectjDescriptorBuilder.WeavingOption weaving()
    {
        return null;
    }

    @Override
    public String exportAsString()
    {
        return null;
    }
}
