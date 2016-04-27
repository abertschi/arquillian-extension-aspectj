package ch.abertschi.arquillian;

import ch.abertschi.arquillian.util.ResolverUtil;
import com.github.underscore.$;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.util.List;

/**
 * Created by abertschi on 13/04/16.
 */
public class PatterTests
{
    private static final String AJRT = "org.aspectj:aspectjrt:1.8.9";

    public static void main(String[] args)
    {
        AntPathMatcher m = new AntPathMatcher();
//        String a = "/**/ch/abertschi/arquillian/JarDeploymentIT*";
//        String b = "/asdf/ch/abertschi/arquillian/JarDeploymentIT$SayArquillianAspect.class";

        String a ="**/test";
        String b = "asdf/test";
        System.out.println(m.match(a, b));

    }
}
