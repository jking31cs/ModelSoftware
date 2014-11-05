package com.cs6491final;

import processing.core.PApplet;
/**
 * Simple class to hold a point in 2D space.
 * @author jking31
 */
public class Point {
	public double x;
	public double y;
	public double z;
	
	private PApplet p;
	
	public Point(double x, double y, PApplet p) {
		this.x = x;
		this.y = y;
		this.z = 0;
		this.p=p;
	}
	
	public Point(double x, double y, double z, PApplet p){
		this.x=x;
		this.y=y;
		this.z=z;
		this.p=p;
	}
	
	public void drawPoint2D(){
		p.fill(0,255,0);
		p.ellipse((float)x, (float)y, 20, 20);
		p.fill(0);
	}
	
	public void drawPoint3D(){
		p.pushMatrix();
		p.fill(0, 255, 0);
		p.translate((float)x, (float)y, (float)z);
		p.sphere(5);
		p.popMatrix();
	}

	@Override
	public String toString() {
		return "Point: (" + x + "," + y + "," + z + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point point = (Point) o;

		if (Double.compare(point.x, x) != 0) return false;
		if (Double.compare(point.y, y) != 0) return false;
		if (Double.compare(point.z, z) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
