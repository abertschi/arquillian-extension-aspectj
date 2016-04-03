package ch.abertschi.arquillian.util;

import com.github.underscore.$;

import java.util.Collections;
import java.util.List;

/**
 * Created by abertschi on 03/04/16.
 */
public class MatcherUtils
{
    public static String transformToMatchAnyParent(String pattern)
    {
        if (!pattern.startsWith("/") && !pattern.startsWith("*"))
        {
            pattern = "**/" + pattern;
        }
        return pattern;
    }

    public static String transformToMatchAnyChild(String pattern)
    {
        if (!pattern.endsWith("/") && !pattern.endsWith("*"))
        {
            pattern = pattern + "/**";
        }
        return pattern;
    }

    public static List<String> transformToMatchAnyChild(List<String> patterns)
    {
        return Collections.unmodifiableList($.map(patterns, s -> transformToMatchAnyChild(s)));
    }
}
