package ch.abertschi.aspectj;

import java.lang.annotation.*;

/**
 * Created by abertschi on 03/07/16.
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor
{
}
