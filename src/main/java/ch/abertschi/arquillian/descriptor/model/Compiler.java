package ch.abertschi.arquillian.descriptor.model;

/**
 * Created by abertschi on 01/04/16.
 */
public class Compiler
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
