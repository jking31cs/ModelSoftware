package edu.cs6491Final;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by bobby on 11/8/14.
 */
public class CircularAxisTest {

	@Test
	public void testClosestProject() {
		Point origin = new Point(200,100,0);
		Point center = new Point(250,100,0);

		CircularAxis axis = new CircularAxis(
			origin, center
		);

		assertEquals(50, axis.radius, .05);

		assertEquals(new Point(200,100,0), axis.closestProject(new Point(175,100,0)));

		Point closeP = axis.closestProject(new Point(150,0,0));

		assertEquals(214.6447, closeP.x, .01);
		assertEquals(64.6447, closeP.y, .01);
	}

	@Test
	public void testTangentVec() {
		Point origin = new Point(200,100,0);
		Point center = new Point(250,100,0);

		CircularAxis axis = new CircularAxis(
			origin, center
		);

		Point p = axis.closestProject(new Point(175,100,0));

		Vector tangent = axis.tangentVectorAt(p);
		assertEquals(0, tangent.x, .001);
		assertEquals(1, tangent.y, .001);
		assertEquals(0, tangent.z, .001);

		p = axis.closestProject(new Point(150,0,0));
		tangent = axis.tangentVectorAt(p);
		assertEquals(Math.sqrt(2)/-2, tangent.x, .001);
		assertEquals(Math.sqrt(2)/2, tangent.y, .001);
		assertEquals(0, tangent.z, .001);
	}
}
