package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;
import ch.abertschi.arquillian.domain.DummyGreeter;
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

import javax.ejb.EJB;
import javax.inject.Inject;


@RunWith(Arquillian.class)
public class BasicIT
{
    @Aspect
    private static class SayArquillianAspect
    {
        @Around("call(* *..DummyGreeter*..(..))")
        public Object sayArquillian()
        {
            return "arquillian!";
        }
    }

    @Inject
    DummyGreeter greeter;

    @Deployment
    public static Archive<?> deploy()
    {
        String json = AspectjDescriptor
                .create()
                .weave("ejb.jar")
                .include(DummyGreeter.class)
                .withAspectsInWeavingLibrary()
                .include(SayArquillianAspect.class)
                .addAspects()
                .and()
                .exportAsString();

        return ShrinkWrap.create(JavaArchive.class, "ejb.jar")
                .addClass(DummyGreeter.class)
                .addClass(SayArquillianAspect.class)
                .addAsManifestResource(new StringAsset(json), "aspectj.json")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.json");
    }

    @Test
    public void test_around_aspect()
    {
        String expected = "arquillian !";

        Assert.assertEquals(greeter.sayAspect(), expected);
    }
}

