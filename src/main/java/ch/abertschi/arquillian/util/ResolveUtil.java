package ch.abertschi.arquillian.util;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 28/03/16.
 */
public class ResolveUtil
{
    private static ResolveUtil instance;

    public static String MVN_OFFLINE = "MVN_OFFINE";
    public static String MVN_SETTINGS = "MVN_SETTINGS_PATH";

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

    public List<Archive<?>> resolveWithMaven(String name)
    {
        List<Archive<?>> results = new ArrayList<>();
        for (JavaArchive jar : Maven.resolver().resolve(name).withTransitivity().asList(JavaArchive.class))
        {
            results.add(jar);
        }
        return results;
    }
}
