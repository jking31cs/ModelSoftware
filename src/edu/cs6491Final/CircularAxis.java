package edu.cs6491Final;

import processing.core.PApplet;

/**
 * Meant to be a circular axis
 */
public class CircularAxis extends Axis {

	public final Point center;
	public final double radius;

	public CircularAxis(Point origin, Point center) {
		super(origin);
		this.center = center;
		this.radius = center.distanceTo(origin);
	}

	@Override
	public Point closestProject(Point p) {

		//Assuming point and circle are on the same plane, this works
		Vector p1 = center.to(p).normalize().mul(radius);
		return center.add(p1);
	}

	@Override
	public Vector tangentVectorAt(Point p) {
		Vector p_to_c = p.to(center);
		Vector rotV = new Vector(0,0,1); //assuming we're on XY plane.
		return p_to_c.normalize().rotate(Math.PI/2, rotV);
	}

	@Override
	public void draw(PApplet p) {
		//This only holds true when circule is in XY plane
		p.pushMatrix();
		this.origin.draw(p);
		p.noFill();
		p.ellipse((float) center.x, (float) center.y, (float) radius*2f, (float) radius*2f);
		p.popMatrix();
	}
}
