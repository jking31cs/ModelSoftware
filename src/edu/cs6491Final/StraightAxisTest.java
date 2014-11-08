package edu.cs6491Final;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by bobby on 11/8/14.
 */
public class StraightAxisTest {

	@Test
	public void testClosestProjection() {
		StraightAxis a = new StraightAxis(
			new Point(300,600,0),
			new Vector(0,-1,0)
		);

		Point p = new Point(150,240,0);

		Point projection = a.closestProject(p);

		assertEquals(new Point(300, 240, 0), projection);
	}

	@Test
	public void testNormalVector() {
		StraightAxis a = new StraightAxis(
			new Point(300,600,0),
			new Vector(0,-1,0)
		);

		assertEquals(new Vector(0,-1,0), a.tangentVectorAt(new Point(300, 300, 0)));
	}
}
