package ch.abertschi.arquillian;

/**
 * Created by abertschi on 26/03/16.
 */
public class Artifact
{
    private String groupId;
    private String artifactName;
    private String version;
    private String scope;

    public String getGav() {
        String gav = "";

        if (this.groupId != null && this.artifactName != null)
        {
            gav = String.format("%s:%s", this.groupId, this.artifactName);
            if (this.version != null)
            {
                gav = String.format("%s:%s", gav, this.version);
            }
        }
        else if (this.artifactName != null)
        {
            gav = this.getArtifactName();

            if (this.version != null)
            {
                gav = String.format("%s:%s", gav, this.version);
            }
        }
        return gav;
    }

    public String getScope()
    {
        return scope;
    }

    public Artifact setScope(String scope)
    {
        this.scope = scope;
        return this;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public Artifact setGroupId(String groupId)
    {
        this.groupId = groupId;
        return this;
    }

    public String getArtifactName()
    {
        return artifactName;
    }

    public Artifact setArtifactName(String artifactName)
    {
        this.artifactName = artifactName;
        return this;
    }

    public String getVersion()
    {
        return version;
    }

    public Artifact setVersion(String version)
    {
        this.version = version;
        return this;
    }
}
