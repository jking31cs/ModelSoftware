package edu.cs6491Final;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by bobby on 11/8/14.
 */
public class VectorTest {
	@Test
	public void testRotation() {
		Vector v1 = new Vector(10,0,0);
		Vector rotV = new Vector(0,0,1);

		Vector rotate = v1.rotate(Math.PI / 2, rotV);
		assertEquals(
			0,
			rotate.x,
			.001
		);
		assertEquals(
			10,
			rotate.y,
			.001
		);
		assertEquals(
			0,
			rotate.z,
			.001
		);

		v1 = new Vector(-10,-10,0);
		rotate = v1.rotate(Math.PI / 2, rotV);
		assertEquals(
			10,
			rotate.x,
			.001
		);
		assertEquals(
			-10,
			rotate.y,
			.001
		);
		assertEquals(
			0,
			rotate.z,
			.001
		);
	}
}
