package ch.abertschi.arquillian.descriptor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 01/04/16.
 */
public class AspectLibrary
{
    private String name;
    private List<String> includes = new ArrayList<>();
    private List<String> excludes = new ArrayList<>();

    AspectLibrary()
    {
    }

    public AspectLibrary(String name)
    {
        this.name = name;
    }

    public List<String> getIncludes()
    {
        return includes;
    }

    public AspectLibrary setIncludes(List<String> includes)
    {
        this.includes = includes;
        return this;
    }

    public List<String> getExcludes()
    {
        return excludes;
    }

    public AspectLibrary setExcludes(List<String> excludes)
    {
        this.excludes = excludes;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public AspectLibrary setName(String name)
    {
        this.name = name;
        return this;
    }
}
