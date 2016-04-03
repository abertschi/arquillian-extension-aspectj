package ch.abertschi.arquillian.descriptor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 01/04/16.
 */
public class WeavingLibrary
{
    private String name;
    private List<String> includes = new ArrayList<>();
    private List<String> excludes = new ArrayList<>();
    private List<AspectLibrary> aspects = new ArrayList<>();

    WeavingLibrary()
    {
    }

    public WeavingLibrary(String name)
    {
        this.name = name;
    }

    public List<AspectLibrary> getAspects()
    {
        return aspects;
    }

    public WeavingLibrary setAspects(List<AspectLibrary> aspects)
    {
        this.aspects = aspects;
        return this;
    }

    public List<String> getExcludes()
    {
        return excludes;
    }

    public WeavingLibrary setExcludes(List<String> excludes)
    {
        this.excludes = excludes;
        return this;
    }

    public List<String> getIncludes()
    {
        return includes;
    }

    public WeavingLibrary setIncludes(List<String> includes)
    {
        this.includes = includes;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public WeavingLibrary setName(String name)
    {
        this.name = name;
        return this;
    }
}
