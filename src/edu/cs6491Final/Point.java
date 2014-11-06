package edu.cs6491Final;

import processing.core.PApplet;

/**
 * Simple Point class that just holds x,y,z coordinates.
 * @author jking31
 *
 */
public class Point implements Drawable {
	
	public double x,y,z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point add(Vector v) {
		return new Point(x+v.x, y+v.y, z+v.z);
	}
	
	public Vector asVec() {
		return new Vector(x,y,z);
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

	public double distanceTo(Point mousePoint) {
		// TODO Auto-generated method stub
		return this.asVec().sub(mousePoint.asVec()).getMag();
	}

}
