package ch.abertschi.arquillian;

import org.springframework.util.AntPathMatcher;

/**
 * Created by abertschi on 13/04/16.
 */
public class PatterTests
{
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
