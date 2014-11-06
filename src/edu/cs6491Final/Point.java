package edu.cs6491Final;

import processing.core.PApplet;

/**
 * Simple Point class that just holds x,y,z coordinates.
 * @author jking31
 *
 */
public class Point implements Drawable {
	
	public final double x,y,z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * This draws a sphere in 3D space at the point.
	 */
	@Override
	public void draw(PApplet p) {
		// TODO Auto-generated method stub
		p.pushMatrix();
		p.translate((float) x, (float) y, (float) z);
		p.sphere(10);
		p.popMatrix();
	}

}
