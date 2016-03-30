package ch.abertschi.arquillian;

import com.sun.tools.javac.comp.Resolve;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by abertschi on 28/03/16.
 */
public class ResolveUtil
{
    private static ResolveUtil instance;

    private PomEquippedResolveStage mResolver;

    public static ResolveUtil get()
    {
        if (instance == null)
        {
            instance = new ResolveUtil();
        }
        return instance;
    }

    private ResolveUtil()
    {
        mResolver = Maven.resolver().loadPomFromFile("pom.xml");
    }

    protected List<JavaArchive> resolveWithoutTransitivity(Artifact artifact)
    {
        String gav = artifact.getGav();
        if (gav == null && gav.isEmpty())
        {
            throw new RuntimeException("Invalid input. ArtifactName cant be null");
        }

        return Collections.unmodifiableList(Arrays.asList(
                mResolver
                .importTestDependencies()
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withMavenCentralRepo(false)
                .withoutTransitivity().as(JavaArchive.class)));
    }

    protected List<JavaArchive> getTransientDependencies(Artifact artifact)
    {
        String gav = artifact.getGav();
        if (gav == null && gav.isEmpty())
        {
            throw new RuntimeException("Invalid input. ArtifactName cant be null");
        }

        MavenStrategyStage resolver = mResolver
                .importTestDependencies()
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withMavenCentralRepo(false);

        List<JavaArchive> withoutTransitivity = Arrays.asList(resolver.withoutTransitivity()
                .as(JavaArchive.class));

        List<JavaArchive> withTransitivity = Arrays.asList(resolver.withTransitivity()
                .as(JavaArchive.class));

        List<JavaArchive> transientDependencies = new ArrayList<>();

        for (JavaArchive jar : withTransitivity)
        {
            boolean isTransient = true;
            for (JavaArchive notTransientJar : withoutTransitivity)
            {
                if (jar.getName().equals(notTransientJar.getName()))
                {
                    isTransient = false;
                    break;
                }
            }
            if (isTransient)
            {
                transientDependencies.add(jar);
            }
        }
        return Collections.unmodifiableList(transientDependencies);
    }

    private List<String> resolveArtifactGav(String pattern)
    {
        //pattern: groupId:artifactName:version

        List<JavaArchive> libs = new ArrayList<>();
        if (pattern.contains(".jar"))
        {
            String[] split = pattern.split(":");
            if (split.length == 1 && split[0].endsWith(".jar"))
            {
                // Assume that only the artifactName is given, ie: myjar.jar
                Artifact artifact = new Artifact().setArtifactName(split[0]);
                libs.addAll(this.resolveWithoutTransitivity(artifact));
            }
            else if (split.length >= 2 && split[1].endsWith(".jar"))
            {
                // Assume that groupId and artifactName is given. Ie: com.mycompany:myjar.jar
                Artifact artifact = new Artifact().setGroupId(split[0]).setArtifactName(split[1]);
                libs.addAll(this.resolveWithoutTransitivity(artifact));
            }
        }

        List<String> resolved = new ArrayList<>();
        for (JavaArchive jar : libs)
        {
            resolved.add(jar.getId());
        }
        return resolved;
    }
}
