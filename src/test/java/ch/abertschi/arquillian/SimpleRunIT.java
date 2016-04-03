package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;
import ch.abertschi.arquillian.domain.DummyGreeter;
import junit.framework.Assert;
import org.aspectj.lang.ProceedingJoinPoint;
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

/**
 * Created by abertschi on 25/03/16.
 */

@RunWith(Arquillian.class)
public class SimpleRunIT
{

    @Deployment
    public static Archive<?> deploy()
    {

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "my-ear.ear");

        WebArchive war = ShrinkWrap.create(WebArchive.class, "webarchive.war")
                .addClass(DummyGreeter.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        String json = AspectjDescriptor
                .create()
                .weave("webarchive.war")

                .withAspects("webarchive.war")
                        //.exclude("/META**")
                .addAspects()
                .and()
                .exportAsString();

        System.out.println(json);
//
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "mytest.jar")
                .addPackage(SimpleRunIT.class.getPackage());

        war.addAsLibraries(jar);


        ear.addAsModule(war);
        ear.addAsLibraries(jar);
        ear.addAsManifestResource(new StringAsset(json), "aspectj.json");

        return ear;
    }

    @Test
    public void simpleRun()
    {
        Assert.assertTrue(true);
    }

    @Aspect
    private static class DummyAspect
    {
        @Around("call(* *..DummyGreeter*..(..))")
        public Object sayArquillian()
        {
            return "arquillian!";
        }
    }
}
