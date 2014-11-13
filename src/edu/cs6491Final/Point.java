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

	public Vector to(Point p) {
		return new Vector(p.x-x, p.y-y, p.z-z);
	}

	public double distanceTo(Point p) {
		return this.to(p).getMag();
	}

	/**
	 * This draws a sphere in 3D space at the point.
	 */
	@Override
	public void draw(PApplet p) {
		p.pushMatrix();
		p.translate((float) x, (float) y, (float) z);
		p.sphere(10);
		p.popMatrix();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", z=" + z + "]";
	}


	/**
	 * Updates the position of the point to the new point passed in.
	 * @param add
	 */
	public void move(Point p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}
}
