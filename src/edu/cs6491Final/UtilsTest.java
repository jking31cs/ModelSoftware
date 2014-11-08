package edu.cs6491Final;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testMirroring() {
		Point orig = new Point(50,25,0);
		Axis axis = new StraightAxis(new Point (300,600,0),new Vector(0,-1,0));
		
		Point mirroredPoint = Utils.mirrored(orig, axis);
		assertEquals(550, mirroredPoint.x, 1);
		assertEquals(25, mirroredPoint.y, 1);
		assertEquals(0, mirroredPoint.z, 1);
	}
	
	@Test
	public void testRotation() {
		Point orig = new Point(50,25,0);
		Axis axis = new StraightAxis(new Point (300,600,0),new Vector(0,-1,0));
		Point newPoint = Utils.rotate(orig, axis, Math.PI/2);
		assertEquals(300, newPoint.x, 1);
		assertEquals(25, newPoint.y, 1);
		assertEquals(250, newPoint.z, 1);
	}

}
