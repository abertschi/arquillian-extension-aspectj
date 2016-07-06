package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;
import ch.abertschi.arquillian.descriptor.Filters;
import ch.abertschi.arquillian.domain.DummyGreeter;
import ch.abertschi.arquillian.domain.Greeting;
import junit.framework.Assert;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

import javax.inject.Inject;

/**
 * Created by abertschi on 25/03/16.
 */

//@RunWith(Arquillian.class)
public class EarDeploymentIT
{
    // TODO: implement jar in war in ear scenario

//    @Inject
//    DummyGreeter greeter;
//
//    @Deployment
//    public static Archive<?> deploy()
//    {
//        String json = AspectjDescriptor
//                .create()
//                .weave("**/mytests.jar")
//                .filter(Filters.include(Greeting.class.getPackage()))
//                .addWeaveDependency()
//
//                .exportAsString();
//
//        EnterpriseArchive ear = ShrinkWrap
//                .create(EnterpriseArchive.class, "myear.ear");
//
//        WebArchive war = ShrinkWrap.create(WebArchive.class, "mywar.war")
//                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//
//        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "mytests.jar")
//                .addPackages(true, Greeting.class.getPackage())
//                .addPackage(EarDeploymentIT.class.getPackage());
//
//        war.addAsLibraries(jar);
//        ear.addAsModule(war);
//        ear.addAsManifestResource(new StringAsset(json), "aspectj.json");
//
//        return ear;
//    }
//
//    @Test
//    public void test_around_aspect()
//    {
//        String expected = "arquillian!";
//        System.out.println(greeter.getGreeting());
//        Assert.assertEquals(greeter.getGreeting(), expected);
//    }
}
