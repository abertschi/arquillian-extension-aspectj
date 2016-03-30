package ch.abertschi.arquillian;

public enum ArchiveType
{
    EAR,
    WAR,
    JAR,
    UNKNOWN;

    public static ArchiveType getTypeFromName(String name)
    {
        ArchiveType ext = ArchiveType.UNKNOWN;
        if (name.toLowerCase().endsWith(".ear"))
        {
            ext = ArchiveType.EAR;
        }
        else if (name.toLowerCase().endsWith(".war"))
        {
            ext = ArchiveType.WAR;
        }
        else if (name.toLowerCase().endsWith(".jar"))
        {
            ext = ArchiveType.JAR;
        }
        return ext;
    }

    public static ArchiveType fromString(String name)
    {
        ArchiveType ext = ArchiveType.UNKNOWN;
        if (name.toLowerCase().equals("ear"))
        {
            ext = ArchiveType.EAR;
        }
        else if (name.toLowerCase().equals("war"))
        {
            ext = ArchiveType.WAR;
        }
        else if (name.toLowerCase().equals("jar"))
        {
            ext = ArchiveType.JAR;
        }
        return ext;
    }
}
