package ch.abertschi.arquillian.descriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 26/03/16.
 */
public class AspectJDescriptorModel
{

    Weaving weaving = new Weaving();
    Aspects aspects = new Aspects();
    Compiler compiler = new Compiler();

    public static class Weaving
    {

        List<String> includes = new ArrayList<>();

        List<String> excludes = new ArrayList<>();
    }

    public static class Aspects
    {

        List<String> includes = new ArrayList<>();

        List<String> excludes = new ArrayList<>();
    }

    public static class Compiler
    {

        boolean verbose = false;
    }
}
