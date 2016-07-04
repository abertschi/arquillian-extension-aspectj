package ch.abertschi.aspectj;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;

import ch.abertschi.arquillian.descriptor.Filters;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.arquillian.test.api.ArquillianResource;
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
    private static final String BASE = "http://localhost:8080/my-app/";

    @Deployment
    public static Archive deploy()
    {
        String aspectj = AspectjDescriptor
                .create()
                .weave("my-app.war")
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
        // given
        String jokeUrl = BASE + "joke/";

        //when
        HttpResponse<String> joke = Unirest.get(jokeUrl).asString();

        // then
        Assert.assertTrue(joke.getStatus() == 200);
    }

    @Test()
    public void test_execution_status() throws UnirestException
    {
        // given
        String jokeUrl = BASE + "joke/";
        Execution.GET.setExecutionStatus(Execution.ExecutionStatus.NO);

        // when
        HttpResponse<String> joke = Unirest.get(jokeUrl).asString();

        // then
        Assert.assertTrue(joke.getStatus() == 200);
        Assert.assertEquals(Execution.ExecutionStatus.YES, Execution.GET.getExectionStatus());
    }
}
