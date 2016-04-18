package ch.abertschi.arquillian.descriptor;

/**
 * Created by abertschi on 18/04/16.
 */
public class Filter
{
    public enum FilterType
    {
        INCLUDE, EXCLUDE
    }

    private FilterType type;

    private String filter;

    public Filter(FilterType type, String filterPattern)
    {
        this.type = type;
        this.filter = filterPattern;
    }

    public FilterType getType()
    {
        return type;
    }

    public Filter setType(FilterType type)
    {
        this.type = type;
        return this;
    }

    public String getFilter()
    {
        return filter;
    }

    public Filter setFilter(String filter)
    {
        this.filter = filter;
        return this;
    }
}
