package ch.abertschi.arquillian;

import ch.abertschi.arquillian.domain.Greeting;
import com.github.underscore.$;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;

/**
 * Created by abertschi on 13/04/16.
 */
public class AjCompilerTest
{
    @Aspect
    public static class SayArquillianAspect
    {
        @Around("call(* ch.abertschi.arquillian.domain..*(..))")
        public Object doNothing()
        {
            return "arquillian!";
        }
    }

    @Test
    public void test_simple_weaving()
    {
        // given
        JavaArchive weaving = ShrinkWrap.create(JavaArchive.class, "weaving.jar")
                .addPackage(Greeting.class.getPackage())
                .addClass(SayArquillianAspect.class);

        JavaArchive aspects = ShrinkWrap.create(JavaArchive.class, "aspects.jar")
                .addClass(SayArquillianAspect.class);

        AjCompiler c = new AjCompiler();

        // when
        Archive<?> weaved = c.compileTimeWeave(weaving, new ArrayList<Archive<?>>());
        $.forEach(weaved.getContent().keySet(), o -> System.out.println(o));

        // then
        Assert.notNull(weaved.get("/ch/abertschi/arquillian/AjCompilerTest$SayArquillianAspect.class"));
    }

    @Test
    public void test_runtime_libs()
    {
        Assert.isTrue(new AjCompiler().getRuntimeLibraries().size() > 0);
    }


}
