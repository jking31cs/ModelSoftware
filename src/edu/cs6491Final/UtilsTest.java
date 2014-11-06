package edu.cs6491Final;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testMirroring() {
		Point orig = new Point(50,25,0);
		Point axisPoint = new Point (300,600,0);
		Vector axis = new Vector(0,-1,0).mul(600);
		
		Point mirroredPoint = Utils.mirrored(orig, axisPoint, axis);
		assertEquals(550, mirroredPoint.x, 1);
		assertEquals(25, mirroredPoint.y, 1);
		assertEquals(0, mirroredPoint.z, 1);
	}
	
	@Test
	public void testRotation() {
		Point orig = new Point(50,25,0);
		Point axisPoint = new Point (300,600,0);
		Vector axis = new Vector(0,-1,0).mul(600);
		
		Point newPoint = Utils.rotate(orig, axisPoint, axis, Math.PI/2);
		assertEquals(300, newPoint.x, 1);
		assertEquals(25, newPoint.y, 1);
		assertEquals(250, newPoint.z, 1);
	}

}
