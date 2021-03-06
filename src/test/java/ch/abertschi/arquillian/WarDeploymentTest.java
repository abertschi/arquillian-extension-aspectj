package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;
import ch.abertschi.arquillian.domain.DummyGreeter;
import ch.abertschi.arquillian.domain.Greeting;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;


@RunWith(Arquillian.class)
public class WarDeploymentTest
{
    @Inject
    DummyGreeter greeter;

    @Deployment
    public static Archive<?> deploy()
    {
        String json = AspectjDescriptor
                .create()
                .weaveRootArchive()
                .addWeaveDependency()
                .exportAsString();

        return ShrinkWrap.create(WebArchive.class)
                .addClass(WarDeploymentTest.class)
                .addPackages(true, Greeting.class.getPackage())
                .addAsManifestResource(new StringAsset(json), "aspectj.json")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void test_around_aspect()
    {
        String expected = "arquillian!";
        System.out.println(greeter.getGreeting());
        Assert.assertEquals(greeter.getGreeting(), expected);
    }
}

