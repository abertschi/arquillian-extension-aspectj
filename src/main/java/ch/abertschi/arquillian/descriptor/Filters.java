package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.util.MatcherUtils;
import com.github.underscore.$;

import java.util.Arrays;
import java.util.List;

/**
 * Created by abertschi on 15/04/16.
 */
public class Filters
{
    public static List<Filter> use(Filter.FilterType type, String... pattern)
    {
        return $.map(Arrays.asList(pattern), p -> new Filter(type, p));
    }

    public static List<Filter> use(Filter.FilterType type, Package... packages)
    {
        return $.map(Arrays.asList(packages), p -> new Filter(type, getPackagePattern(p)));
    }

    public static List<Filter> use(Filter.FilterType type, Class<?>... types)
    {

        return $.map(Arrays.asList(types), t -> new Filter(type, getClassPattern(t)));
    }

    public static List<Filter> include(String... pattern)
    {
        return use(Filter.FilterType.INCLUDE, pattern);
    }

    public static List<Filter> include(Package... packages)
    {
        return use(Filter.FilterType.INCLUDE, packages);
    }

    public static List<Filter> include(Class<?>... types)
    {
        return use(Filter.FilterType.INCLUDE, types);
    }

    public static List<Filter> exclude(String... pattern)
    {
        return use(Filter.FilterType.EXCLUDE, pattern);
    }

    public static List<Filter> exclude(Package... packages)
    {
        return use(Filter.FilterType.EXCLUDE, packages);
    }

    public static List<Filter> exclude(Class<?>... types)
    {
        return use(Filter.FilterType.EXCLUDE, types);
    }

    private static String getClassPattern(Class<?> type)
    {
        return MatcherUtils.transformToMatchAnyParent(
                MatcherUtils.transformToMatchAnySuffixInSameDirectory(
                        packageToFileStructure(type.getCanonicalName())));
    }

    private static String getPackagePattern(Package p)
    {
        return MatcherUtils.transformToMatchAnyParent(
                MatcherUtils.transformToMatchAnyChild(
                        packageToFileStructure(p.getName())));
    }

    private static String packageToFileStructure(String packageName)
    {
        return packageName.replace(".", "/");
    }
}
