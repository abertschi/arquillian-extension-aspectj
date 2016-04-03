package ch.abertschi.arquillian.model;

import ch.abertschi.arquillian.descriptor.AspectjDescriptor;
import org.junit.Test;

/**
 * Created by abertschi on 03/04/16.
 */
public class AspectjDescriptorTest
{
    @Test
    public void test_build_empty()
    {
        String json = AspectjDescriptor.create().toString();
    }
}
