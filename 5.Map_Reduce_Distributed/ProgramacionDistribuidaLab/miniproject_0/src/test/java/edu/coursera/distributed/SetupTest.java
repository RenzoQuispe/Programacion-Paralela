package edu.coursera.distributed;

import org.junit.Test;
import static org.junit.Assert.*;

public class SetupTest {

    /*
     * A simple test case.
     */
    @Test
    public void testSetup() {
        final int result = Setup.setup(42);
        assertEquals(42, result);
    }
}
