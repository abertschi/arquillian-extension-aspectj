package ch.abertschi.arquillian.descriptor;

import com.github.underscore.$;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by abertschi on 18/04/16.
 */
public class FilterBase<T> implements AspectjDescriptorBuilder.FilterOption<T>
{
    private List<Filter> mFilters = new ArrayList<>();

    @Override
    public T filter(Filter... filters)
    {
        return filter(Arrays.asList(filters));
    }

    @Override
    public T filter(List<Filter> filters)
    {
        mFilters.addAll(filters);
        return (T) this;
    }

    @Override
    public T filter(Filter.FilterType type, String... patterns)
    {
        mFilters.addAll($.map(Arrays.asList(patterns), p -> new Filter(type, p)));
        return (T) this;
    }

    public List<Filter> getFilters()
    {
        return mFilters;
    }

    public List<Filter> getIncludeFilters()
    {
        return $.filter(mFilters, filter -> filter.getType() == Filter.FilterType.INCLUDE);
    }

    public List<Filter> getExcludeFilters()
    {
        return $.filter(mFilters, filter -> filter.getType() == Filter.FilterType.EXCLUDE);
    }

    public FilterBase setFilters(List<Filter> filters)
    {
        mFilters = filters;
        return this;
    }
}
