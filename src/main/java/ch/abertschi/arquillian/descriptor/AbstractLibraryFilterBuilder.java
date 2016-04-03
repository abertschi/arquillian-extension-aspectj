package ch.abertschi.arquillian.descriptor;

import ch.abertschi.arquillian.util.MatcherUtils;
import ch.abertschi.arquillian.descriptor.AspectjDescriptorBuilder.LibraryFilterOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 29/03/16.
 */
abstract class AbstractLibraryFilterBuilder<T extends LibraryFilterOption<T>> implements LibraryFilterOption<T>
{
    private String mName;

    private List<String> mIncludes = new ArrayList<>();

    private List<String> mExcludes = new ArrayList<>();

    public AbstractLibraryFilterBuilder(String libraryName)
    {
        this.mName = libraryName;
    }

    @Override
    public T include(String pattern)
    {
        mIncludes.add(pattern);
        return (T) this;
    }

    @Override
    public T include(Package packageObject)
    {
        this.include(this.getPackagePattern(packageObject));
        return (T) this;
    }

    @Override
    public T include(Class<?>... types)
    {
        for (Class<?> type : types)
        {
            this.include(this.getClassPattern(type));
        }
        return (T) this;
    }

    @Override
    public T exclude(String pattern)
    {
        mExcludes.add(pattern);
        return (T) this;
    }

    @Override
    public T exclude(Package packageObject)
    {
        this.exclude(this.getPackagePattern(packageObject));
        return (T) this;
    }

    @Override
    public T exclude(Class<?>... types)
    {
        for (Class<?> type : types)
        {
            this.exclude(this.getClassPattern(type));
        }
        return (T) this;
    }

    // -----------------------------------------
    // helper section
    // -----------------------------------------

    protected String getClassPattern(Class<?> type)
    {
        return this.matchAnyParent(this.suffixToMatchAnyExtension(this.packageStructureToFileStructure(type.getCanonicalName())));
    }

    protected String packageStructureToFileStructure(String packageStructure)
    {
        return packageStructure.replace(".", "/");
    }

    protected String getPackagePattern(Package p)
    {
        return this.matchAnyParent(this.matchAnyChild(this.packageStructureToFileStructure(p.getName())));
    }

    protected String matchAnyParent(String name)
    {
        return MatcherUtils.transformToMatchAnyParent(name);
    }

    protected String matchAnyChild(String name)
    {
        return MatcherUtils.transformToMatchAnyChild(name);
    }

    protected String suffixToMatchAnyExtension(String name)
    {
        if (!name.endsWith("*") || !name.endsWith("/"))
        {
            name = name + "*";
        }
        return name;
    }

    protected String matchAnything()
    {
        return "**/*";
    }

    public List<String> getExcludes()
    {
        return mExcludes;
    }

    public AbstractLibraryFilterBuilder setExcludes(List<String> excludes)
    {
        this.mExcludes = excludes;
        return this;
    }

    public List<String> getIncludes()
    {
        return mIncludes;
    }

    public AbstractLibraryFilterBuilder setIncludes(List<String> includes)
    {
        this.mIncludes = includes;
        return this;
    }

    public String getName()
    {
        return mName;
    }

    public AbstractLibraryFilterBuilder setName(String name)
    {
        this.mName = name;
        return this;
    }
}

