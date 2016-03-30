package ch.abertschi.arquillian;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * Created by abertschi on 25/03/16.
 */
public class AspectJExtension implements LoadableExtension
{
    @Override
    public void register(ExtensionBuilder builder)
    {
        //builder.service(ApplicationArchiveProcessor.class, AspectJConfigExtractor.class);
    }
}
