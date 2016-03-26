package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectJDescriptorModel;

/**
 * Created by abertschi on 26/03/16.
 */
public enum Configuration
{
    GET;

    private AspectJDescriptorModel configurationModel;

    public AspectJDescriptorModel getConfigurationModel()
    {
        return configurationModel;
    }

    public Configuration setConfigurationModel(AspectJDescriptorModel configurationModel)
    {
        this.configurationModel = configurationModel;
        return this;
    }
}
