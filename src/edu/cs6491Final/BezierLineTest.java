package edu.cs6491Final;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by jking31 on 11/13/14.
 */
public class BezierLineTest {

    @Test
    public void testChooseMethod() {
        assertEquals(1, BezierLine.choose(3,0), 0);
        assertEquals(3, BezierLine.choose(3,1), 0);
        assertEquals(3, BezierLine.choose(3,2), 0);
        assertEquals(1, BezierLine.choose(3,3), 0);

        assertEquals(252, BezierLine.choose(10,5), 0);

    }
}
