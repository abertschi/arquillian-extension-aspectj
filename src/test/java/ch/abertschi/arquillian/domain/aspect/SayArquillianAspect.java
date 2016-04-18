package ch.abertschi.arquillian.domain.aspect;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by abertschi on 13/04/16.
 */
@Aspect
public class SayArquillianAspect
{
    @Around("call(* ch.abertschi.arquillian.domain.Greeting.*(..))")
    public Object doNothing()
    {
        return "arquillian!";
    }
}
