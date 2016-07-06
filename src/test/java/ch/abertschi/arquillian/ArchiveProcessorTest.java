package ch.abertschi.arquillian;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;
import ch.abertschi.arquillian.descriptor.Filters;
import ch.abertschi.arquillian.domain.Greeting;
import junit.framework.Assert;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.junit.Test;

/**
 * Created by abertschi on 06/07/16.
 */
public class ArchiveProcessorTest
{
    @Test
    public void test_empty_compilation()
    {
        Archive archive = getArchive(AspectjDescriptor
                .create()
                .weaveRootArchive()
                .addWeaveDependency()
                .exportAsString());

        // aspect runtime libs must be added to archive. therefore, size must be bigger
        // after compilation
        int sizeBefore = archive.getContent().size();
        ArchiveProcessor processor = new ArchiveProcessor();
        processor.process(archive, null);

        Assert.assertTrue(sizeBefore < archive.getContent().size());
    }


    @Test
    public void test_with_exclusion_all()
    {
        Archive archive = getArchive(AspectjDescriptor
                .create()
                .weaveRootArchive()
                .filter(Filters.include("not-in-archive"))
                .addWeaveDependency()
                .exportAsString());

        ArchiveProcessor processor = new ArchiveProcessor();
        processor.process(archive, null);
        Assert.assertTrue(true); // no exception
    }

    @Test(expected = RuntimeException.class)
    public void test_weaving_archive_not_found()
    {
        Archive archive = getArchive(AspectjDescriptor
                .create()
                .weave("not-found")
                .addWeaveDependency()
                .exportAsString());

        ArchiveProcessor processor = new ArchiveProcessor();
        processor.process(archive, null);
    }

    @Test(expected = RuntimeException.class)
    public void test_aspect_lib_not_found()
    {
        Archive archive = getArchive(AspectjDescriptor
                .create()
                .weaveRootArchive()
                .aspectLibrary("not_found")
                .addAspectLibrary()
                .addWeaveDependency()
                .exportAsString());

        ArchiveProcessor processor = new ArchiveProcessor();
        processor.process(archive, null);
    }

    @Test(expected = NoResolvedResultException.class)
    public void test_aspect_gav_not_found()
    {
        Archive archive = getArchive(AspectjDescriptor
                .create()
                .weaveRootArchive()
                .aspectLibrary("g:a:v")
                .addAspectLibrary()
                .addWeaveDependency()
                .exportAsString());

        ArchiveProcessor processor = new ArchiveProcessor();
        processor.process(archive, null);
    }

    private Archive getArchive(String json)
    {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(JarDeploymentTest.class)
                .addAsManifestResource(new StringAsset(json), "aspectj.json");
    }
}
