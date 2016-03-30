package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectJDescriptor;
import ch.abertschi.arquillian.descriptor.AspectJDescriptorModel;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by abertschi on 25/03/16.
 */

@RunWith(Arquillian.class)
public class SimpleRunIT
{

    @Deployment
    public static Archive<?> deploy()
    {
//        WebArchive war = ShrinkWrap.create(WebArchive.class)
//                .addClass(DummyGreeter.class)
//                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        String json = AspectJDescriptor
                .create()
                .weavingLibrary("mytest.jar")
                .add()
                .aspectLibrary("mytest.jar")
                .add()
                .exportAsString();
//
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "mytest.jar")
                .addPackage(SimpleRunIT.class.getPackage())
                .addAs
                .addAsManifestResource(new StringAsset(json), "aspectj.json");
//
//
//        war.addAsLibraries(jar);
//
//        return ShrinkWrap.create(EnterpriseArchive.class)
//                .addAsLibraries(jar)
//                .addAsModule(war);

        return jar;
    }

    @Test
    public void simpleRun() {
        Assert.assertTrue(true);
    }

}
