package ch.abertschi.arquillian.descriptor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 26/03/16.
 */
public class AspectjDescriptorModel
{
    private List<WeavingLibrary> weaving = new ArrayList<>();

    public List<WeavingLibrary> getWeaving()
    {
        return weaving;
    }

    public AspectjDescriptorModel setWeaving(List<WeavingLibrary> weaving)
    {
        this.weaving = weaving;
        return this;
    }
}
