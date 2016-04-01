package ch.abertschi.arquillian.descriptor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 26/03/16.
 */
public class AspectJDescriptorModel
{
    private List<WeavingLibrary> weaving = new ArrayList<>();
    private Compiler compiler = new Compiler();

    public List<WeavingLibrary> getWeaving()
    {
        return weaving;
    }

    public AspectJDescriptorModel setWeaving(List<WeavingLibrary> weaving)
    {
        this.weaving = weaving;
        return this;
    }

    public Compiler getCompiler()
    {
        return compiler;
    }

    public AspectJDescriptorModel setCompiler(Compiler compiler)
    {
        this.compiler = compiler;
        return this;
    }
}
