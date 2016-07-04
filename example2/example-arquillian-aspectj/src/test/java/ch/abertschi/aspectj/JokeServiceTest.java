package ch.abertschi.aspectj;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;

import ch.abertschi.arquillian.descriptor.Filters;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.io.File;
import java.net.URL;

/**
 * Created by abertschi on 03/07/16.
 */
@RunWith(Arquillian.class)
public class JokeServiceTest
{

    @EJB
    private JokeService service;

    @Deployment(testable = false)
    public static Archive deploy()
    {
        String aspectj = AspectjDescriptor
                .create()
                .weave()
                .addWeaveDependency()
                .exportAsString();


        return ShrinkWrap.create(WebArchive.class, "my-app.war")
                .addPackage(JokeServiceTest.class.getPackage())
                .addPackage(JokeService.class.getPackage())
                .addAsManifestResource(new StringAsset(aspectj), "aspectj.json")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .importRuntimeDependencies()
                        .resolve()
                        .withTransitivity()
                        .asList(JavaArchive.class));
    }

    @Test()
    public void test_rest() throws UnirestException
    {
        String base = new String("http://localhost:" + System.getProperty("tomee.httpPort"));
        String jokeUrl = String.format("%s/my-app/joke", base);

        HttpResponse<String> joke = Unirest.get(jokeUrl).asString();
        Assert.assertTrue(joke.getStatus() == 200);

    }

    @Test()
    public void test_execution_status()
    {
        Execution.GET.setExecutionStatus(Execution.ExecutionStatus.NO);
        String joke = service.tell();
        Assert.assertNotNull(joke);
        System.out.println(joke);
        Assert.assertEquals(Execution.ExecutionStatus.YES, Execution.GET.getExectionStatus());
    }
}
