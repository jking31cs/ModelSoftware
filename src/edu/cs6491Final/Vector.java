package edu.cs6491Final;

import static java.lang.Math.*;
import processing.core.PApplet;

/**
 * Unlike a Point, a vector represents more of a direction than a point in space.
 * @author jking31
 */
public class Vector{
	public final double x;
	public final double y;
	public final double z;
  
	public Vector(double a,double b){
		x=a;
		y=b;
		z=0;
	}	
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
  
	/**
	 * Gets the magnitude of the Vector.
	 * @return
	 */
	public double getMag(){
	    return sqrt( x*x + y*y + z*z);
	}
	
	/**
	 * Normalizes the vector.
	 * @return
	 */
	public Vector normalize(){
		return this.mul(1/getMag());
	}
	
	/**
	 * Scalar multiplication of vector.
	 * @param c
	 * @return
	 */
	public Vector mul(double c){
		return new Vector(x*c,y*c,z*c);
 	}
	
	/**
	 * Adds the vector to this vector.
	 * @param v
	 * @return
	 */
	public Vector add(Vector v) {
		return new Vector(v.x+x, v.y+y, v.z+z);
	}
	
	public Vector add(double s) {
		return new Vector(x+s, y+s, z+s);
	}
	
	/**
	 * Subtracts the vector from this vector.
	 * @param v
	 * @return
	 */
	public Vector sub(Vector v) {
		return new Vector(x-v.x, y-v.y, z-v.z);
	}
	
	/**
	 * Rotates the vector by the given angle which is in radians in the plane (i,j).
	 * Assumes that i,j are orthogonal
	 * @param angle
	 * @return
	 */
	public Vector rotate(double angle, Vector i, Vector j) {
		double x = this.dotProduct(i);
		double y = this.dotProduct(j);
		Vector a = i.mul(x*cos(angle) - y*sin(angle));
		Vector b = j.mul(x*sin(angle) + y*cos(angle));
		return a.add(b);
	}
	
	/**
	 * Returns the dot product.
	 * @param v
	 * @return
	 */
	public double dotProduct(Vector v) {
		return v.x*x + v.y*y + v.z*z;
	}
	
	/**
	 * Vector or cross product, returns the determinate of 
	 * 		| x, v.x |
	 * 		| y, v.y |
	 * @param v
	 * @return
	 */
	public double determinant2(Vector v) {
		return x*v.y - y*v.x;
	}
	
	public double determinant3(Vector v) {
		return sqrt(this.dotProduct(this) * v.dotProduct(v) - this.dotProduct(v));
	}
	
	public Vector crossProd(Vector v) {
		return new Vector(this.y*v.z - this.z*v.y, this.x*v.z - this.z*v.x, this.x*v.y - this.y*v.x);
	}
	
	public double angleBetween(Vector v) {
		double dotProduct = normalize().dotProduct(v.normalize());

		//Floating point conversion makes our dot product slightly too large/small at min/max case.
		if (abs(dotProduct + 1) < .005) { //In case dotproduct is -1.0000000002, acos returns NaN
			dotProduct = -1d;
		} else if (abs(dotProduct - 1) < .005) { //In case dotproduct is 1.000000002, acos returns NaN 
			dotProduct = 1;
		}


		double angle = Math.acos(dotProduct);
		return angle;
	}
	
	/**
	 * Rotates this vector around a given vector by the given angle.
	 */
	public Vector rotate(double angle, Vector rotVec) {
		Vector u = rotVec.normalize();

		//Using Rodrigues' Rotation Formula
		Matrix3D m1 = Matrix3D.I.mul(cos(angle));
		Matrix3D m2 = Matrix3D.crossMulMatrix(u).mul(sin(angle));
		Matrix3D m3 = Matrix3D.tensorProductSelf(u).mul(1 - cos(angle));

		Matrix3D rotMatrix = m1.add(m2).add(m3);

		return rotMatrix.mul(this);

		
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

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
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
		return "Vector{" +
			"x=" + x +
			", y=" + y +
			", z=" + z +
			'}';
	}

	public void draw(PApplet p, Point point){
		p.beginShape(p.LINES);
		p.vertex((float)point.x,(float)point.y, (float)point.z);
		p.vertex((float)(point.x + x*50), (float)(point.y+y*50), (float)(point.z+z*50));
		p.endShape();
	}

}
