package edu.cs6491Final;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void testMirroringStraight() {
		Point orig = new Point(50,25,0);
		Axis axis = new StraightAxis(new Point (300,600,0),new Vector(0,1,0));
		
		Point mirroredPoint = Utils.mirrored(orig, axis);
		assertEquals(550, mirroredPoint.x, 1);
		assertEquals(25, mirroredPoint.y, 1);
		assertEquals(0, mirroredPoint.z, 1);
	}

	@Test
	public void testMirroringCircular() {
		Point orig = new Point(175,100,0);
		Axis axis = new CircularAxis(new Point (200,100,0),new Point(250,100,0));

		Point mirroredPoint = Utils.mirrored(orig, axis);
		assertEquals(225, mirroredPoint.x, .01);
		assertEquals(100, mirroredPoint.y, .01);
		assertEquals(0, mirroredPoint.z, .01);

		orig = new Point(250,25,0);
		mirroredPoint = Utils.mirrored(orig, axis);
		assertEquals(250, mirroredPoint.x, .01);
		assertEquals(75, mirroredPoint.y, .01);
		assertEquals(0, mirroredPoint.z, .01);

	}
	
	@Test
	public void testRotation() {
		Point orig = new Point(50,25,0);
		Axis axis = new StraightAxis(new Point (300,600,0),new Vector(0,1,0));
		Point newPoint = Utils.rotate(orig, axis, Math.PI/2);
		assertEquals(300, newPoint.x, 1);
		assertEquals(25, newPoint.y, 1);
		assertEquals(250, newPoint.z, 1);
	}

}
