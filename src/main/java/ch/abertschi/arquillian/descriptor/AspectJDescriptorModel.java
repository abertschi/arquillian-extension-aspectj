package ch.abertschi.arquillian.descriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 26/03/16.
 */
public class AspectJDescriptorModel
{
    private List<Library> weaving = new ArrayList<>();
    private List<Library> aspects = new ArrayList<>();
    private Compiler compiler = new Compiler();

    public List<Library> getWeaving()
    {
        return weaving;
    }

    public AspectJDescriptorModel setWeaving(List<Library> weaving)
    {
        this.weaving = weaving;
        return this;
    }

    public List<Library> getAspects()
    {
        return aspects;
    }

    public AspectJDescriptorModel setAspects(List<Library> aspects)
    {
        this.aspects = aspects;
        return this;
    }

    public Compiler getCompiler()
    {
        return compiler;
    }

    public AspectJDescriptorModel setCompiler(Compiler compiler)
    {
        this.compiler = compiler;
        return this;
    }

    public static class Library
    {

        String name;
        List<String> includes = new ArrayList<>();
        List<String> excludes = new ArrayList<>();

        public String getName()
        {
            return name;
        }

        public Library setName(String name)
        {
            this.name = name;
            return this;
        }

        public List<String> getExcludes()
        {
            return excludes;
        }

        public Library setExcludes(List<String> excludes)
        {
            this.excludes = excludes;
            return this;
        }

        public List<String> getIncludes()
        {
            return includes;
        }

        public Library setIncludes(List<String> includes)
        {
            this.includes = includes;
            return this;
        }

    }

    public static class Compiler
    {
        boolean verbose = false;

        public boolean isVerbose()
        {
            return verbose;
        }

        public Compiler setVerbose(boolean verbose)
        {
            this.verbose = verbose;
            return this;
        }
    }
}
