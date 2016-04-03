package ch.abertschi.arquillian;

import com.github.underscore.$;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by abertschi on 03/04/16.
 */
public class ArchiveSearch
{
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static List<String> SUPPORTED_NESTED_CONTAINERS = Arrays.asList(".jar", ".war");

    public static List<ArchiveSearchResult> searchInArchive(Archive<?> archive, String pattern)
    {
        String name = archive.getName();
        List<ArchiveSearchResult> returns = _searchInArchive(archive, name, pattern);

        if (MATCHER.match(pattern, name))
        {
            System.out.println("matched!");
            ArchiveSearchResult result = new ArchiveSearchResult()
                    .setPath(name)
                    .setArchive(archive)
                    .setParentArchive(null);

            returns.add(result);
        }
        return returns;
    }

    private static List<ArchiveSearchResult> _searchInArchive(Archive<?> archive, String basepath, String pattern)
    {
        List<ArchiveSearchResult> returns = new ArrayList<>();
        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
        {
            String path = basepath + entry.getKey().get().toString();
            System.out.println("comparing " + pattern + " with " + path);

            if (MATCHER.match(pattern, path))
            {
                Archive<?> subArchive = convertToArchive(entry.getValue().getAsset());
                System.out.println("matched!");
                ArchiveSearchResult result = new ArchiveSearchResult()
                        .setPath(path)
                        .setArchive(subArchive)
                        .setParentArchive(archive);

                returns.add(result);
            }

            if (isNestedContainer(path))
            {
                Archive<?> subArchive = convertToArchive(entry.getValue().getAsset());

                List<ArchiveSearchResult> subResults = _searchInArchive(subArchive, path, pattern);
                if (subResults != null && subResults.size() > 0)
                {
                    returns.addAll(subResults);
                }
            }
        }
        return returns;
    }

    public static Archive<?> filterArchive(Archive<?> archive, List<String> includes, List<String> excludes)
    {
        boolean hasIncludeFilter = false;
        boolean hasExcludeFilter = false;
        if (!$.isEmpty(includes))
        {
            hasIncludeFilter = true;
        }
        if (!$.isEmpty(excludes))
        {
            hasExcludeFilter = true;
        }
        Archive<?> filteredArchive = archive.shallowCopy();
        if (hasIncludeFilter || hasExcludeFilter)
        {
            for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet())
            {
                boolean isFile = entry.getValue().getAsset() == null;
                if (isFile)
                {
                    if (hasIncludeFilter && !matchesPattern(entry.getKey().get(), includes)
                            || hasExcludeFilter && matchesPattern(entry.getKey().get(), excludes))
                    {
                        System.out.println("Removing from " + archive.getName() + " " + entry.getKey());
                        filteredArchive.delete(entry.getKey());
                    }
                }
            }
        }
        return filteredArchive;
    }

    public static Archive<?> replaceArchive(Archive<?> archive, String searchKey, Archive<?> replace)
    {
        String basename = archive.getName();
        if (searchKey.equals(basename))
        {
            return replace;
        }
        else
        {
            Archive<?> replaced = _replaceArchive(archive, searchKey, basename, replace);
            if ($.isNull(replaced))
            {
                String msg = String.format("No entry found with Node %s to replace in %s", searchKey, basename);
                throw new RuntimeException(msg);

            }
            return replaced;
        }
    }

    private static Archive<?> _replaceArchive(Archive<?> parentArchive, String key, String basepath, Archive<?> replace)
    {
        Archive<?> copy = parentArchive.shallowCopy();
        Archive<?> match = null;
        for (Map.Entry<ArchivePath, Node> entry : parentArchive.getContent().entrySet())
        {
            String path = basepath + entry.getKey().get().toString();
            if (key.equals(path))
            {
                System.out.println(String.format("Replacing %s with %s", key, replace.getName()));
                copy.delete(entry.getKey());
                copy.add(replace, entry.getKey(), ZipExporter.class);
                match = copy;
                break;
            }
            else if (key.contains(path))
            {
                if (isNestedContainer(path))
                {
                    Archive<?> subArchive = convertToArchive(entry.getValue().getAsset());
                    Archive<?> subReplace = _replaceArchive(subArchive, key, path, replace);
                    if (!$.isNull(subReplace))
                    {
                        System.out.println(String.format("Replacing %s with %s", key, replace.getName()));
                        copy.delete(entry.getKey());
                        copy.add(subReplace, entry.getKey(), ZipExporter.class);
                        match = copy;
                        break;
                    }
                }
            }
        }
        return match;
    }

    public static boolean matchesPattern(String path, List<String> patterns)
    {
        boolean matches = false;
        for (String pat : patterns)
        {
            if (MATCHER.match(pat, path))
            {
                matches = true;
                break;
            }
        }
        return matches;
    }

    private static boolean isNestedContainer(String name)
    {
        return !$.isEmpty($.filter(SUPPORTED_NESTED_CONTAINERS, ext -> name.endsWith(ext)));
    }

    private static GenericArchive convertToArchive(Asset asset)
    {
        return ShrinkWrap.create(ZipImporter.class)
                .importFrom(asset.openStream()).as(GenericArchive.class);
    }

    public static class ArchiveSearchResult
    {
        private Archive<?> parentArchive;

        private String path;

        private Archive<?> archive;

        public Archive<?> getArchive()
        {
            return archive;
        }

        public ArchiveSearchResult setArchive(Archive<?> archive)
        {
            this.archive = archive;
            return this;
        }

        public Archive<?> getParentArchive()
        {
            return parentArchive;
        }

        public ArchiveSearchResult setParentArchive(Archive<?> parentArchive)
        {
            this.parentArchive = parentArchive;
            return this;
        }

        public String getPath()
        {
            return path;
        }

        public ArchiveSearchResult setPath(String path)
        {
            this.path = path;
            return this;
        }
    }
}
