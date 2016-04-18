package ch.abertschi.arquillian.domain;

import org.jboss.arquillian.core.api.annotation.Inject;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Created by abertschi on 13/04/16.
 */
@Singleton
@Startup
public class DummyGreeter
{
    @Inject
    Greeting greeting;

    @PostConstruct
    public void sayGreeting() {
        System.out.println(greeting.greet());
    }

    public String getGreeting() {
        return greeting.greet();
    }
}
