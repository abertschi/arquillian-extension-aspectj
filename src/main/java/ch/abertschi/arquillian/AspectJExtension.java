package ch.abertschi.arquillian;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * Created by abertschi on 25/03/16.
 */
public class AspectJExtension implements LoadableExtension
{
    @Override
    public void register(ExtensionBuilder builder)
    {
        builder.service(AuxiliaryArchiveProcessor.class, AspectJArchiveProcessor.class);
    }
}
