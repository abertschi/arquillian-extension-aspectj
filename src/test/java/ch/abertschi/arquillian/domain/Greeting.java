package ch.abertschi.arquillian.domain;

import javax.ejb.Startup;
import javax.ejb.Stateless;

/**
 * Created by abertschi on 25/03/16.
 */
@Stateless
@Startup
public class Greeting
{
    public String greet()
    {
        return "aspect";
    }
}
