package edu.cs6491Final;

/**
 * Meant to hold the axis that we will be rotating around
 */
public abstract class Axis implements Drawable {

	public final Point origin;

	protected Axis(Point origin) {
		this.origin = origin;
	}

	/**
	 * This is meant to find the point on the Axis that is the closest projection to p.
	 * @param p Point in space
	 * @return Point on axis that is shortest distance away.
	 */
	public abstract Point closestProject(Point p);

	/**
	 * This finds the normal vector to the axis at point p (which should be on the axis.)
	 * @param p Point on the axis
	 * @return Vector normal to the axis at point p.
	 */
	public abstract Vector tangentVectorAt(Point p);
}
