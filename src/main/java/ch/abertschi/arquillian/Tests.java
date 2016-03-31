package ch.abertschi.arquillian;

import org.springframework.util.AntPathMatcher;

/**
 * Created by abertschi on 31/03/16.
 */
public class Tests
{

    public static void main(String[] args)
    {
        AntPathMatcher matcher = new AntPathMatcher();
        String pattern = "/WEB-INF/classes/**";
        String path = "/WEB-INF/classes/ch/abertschi/arquillian/DummyGreeter.class";
        System.out.println(matcher.match(pattern, path));

    }
}
