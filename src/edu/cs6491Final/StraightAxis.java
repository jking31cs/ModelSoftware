package edu.cs6491Final;

import processing.core.PApplet;

/**
 * Created by bobby on 11/8/14.
 */
public class StraightAxis extends Axis {

	public final Vector vector;

	public StraightAxis(Point origin, Vector vector) {
		super(origin);
		this.vector = vector;
	}

	@Override
	public Point closestProject(Point p) {

		/*
		 * We need to find a projection on the vector such that we
		 * get a zero dot product.  This equation *should* work.
		 */
		double s_top = vector.dotProduct(origin.asVec().sub(p.asVec()));
		double s_bot = vector.dotProduct(vector);
		double s = s_top / s_bot;

		Vector p1 = origin.asVec().sub(vector.mul(s));
		return new Point(p1.x, p1.y, p1.z);
	}

	@Override
	public Vector tangentVectorAt(Point p) {
		//Since this is straight line, this holds true.
		return vector;
	}

	@Override
	public void draw(PApplet p) {
		p.fill(255,255,0); //Make the origin yellow
		p.stroke(255,255,0);
		this.origin.draw(p);
		p.stroke(0,255,0);
		p.strokeWeight(1);
		Vector secondPointValues = origin.asVec().add(vector.mul(10000)); //Attempting to draw a very long line
		p.line(
			(float) origin.x, (float) origin.y, (float) origin.z,
			(float) secondPointValues.x, (float) secondPointValues.y, (float) secondPointValues.z
		);
	}
}
