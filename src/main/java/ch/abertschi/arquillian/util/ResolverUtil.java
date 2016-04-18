package ch.abertschi.arquillian.util;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.ResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.*;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 28/03/16.
 */
public class ResolverUtil
{
    private static ResolverUtil instance;

    private MavenResolverSystem mResolver;
    private PomEquippedResolveStage mPomEquippedResolver;

    public static ResolverUtil get()
    {
        if (instance == null)
        {
            instance = new ResolverUtil();
        }
        return instance;
    }

    private ResolverUtil()
    {
        if (ResolverConfig.isShrinkwrapResolveViaPlugin())
        {
            mPomEquippedResolver = Maven.configureResolverViaPlugin();
        }
        else if (ResolverConfig.isShrinkwrapResolveViaPom())
        {
            ConfigurableMavenResolverSystem resolver = Maven.configureResolver();
            if (ResolverConfig.isMavenOffline())
            {
                resolver.workOffline();
            }
        }
        else
        {
            mResolver = Maven.resolver();
        }
    }

    public MavenStrategyStage resolve(String... resolves)
    {
        if (ResolverConfig.isShrinkwrapResolveViaPlugin()
                || ResolverConfig.isShrinkwrapResolveViaPom())
        {
            return mPomEquippedResolver.resolve(resolves);
        }
        else
        {
            return mResolver.resolve(resolves);
        }
    }
}
