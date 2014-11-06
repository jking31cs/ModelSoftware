package edu.cs6491Final;

public final class Utils {
	
	/**
	 * Mirrors a point around an axis by rotating the vector formed from the origin to the point PI radians
	 * @param p Point to mirror
	 * @param origin local origin
	 * @param axis axis to rotate around
	 * @return new Point that is rotated around PI radians
	 */
	public static Point mirrored(Point p, Point origin, Vector axis) {
		
		return rotate(p,origin,axis,Math.PI);
	}

	public static Point rotate(Point p, Point origin, Vector axis, double theta) {
		Vector v = new Vector(p.x - origin.x, p.y - origin.y, p.z - origin.z);
		Vector rotated = v.rotate(theta, axis.normalize());		
		return new Point(rotated.x + origin.x, rotated.y + origin.y, rotated.z + origin.z);
		
	}

	/**
	 * Does a linear interpolation to form a new PolyLoop going from l1 to l2 while rotating around 
	 * axis starting at origin.
	 * 
	 * TODO make this work with non-straight axis.
	 * @param origin
	 * @param axis
	 * @param l1
	 * @param l2
	 * @param s
	 * @return
	 */
	public static PolyLoop lerp(Point origin, Vector axis, PolyLoop l1, PolyLoop l2, double s) {
		PolyLoop toRet = new PolyLoop();
		for (int i = 0; i < l1.points.size(); i++) {
			Point p1 = l1.points.get(i);
			Point p2 = l2.points.get(i);
			Point newPoint = rotate(new Point(
				p1.x * (1d-s) + p2.x *s,
				p1.y * (1d-s) + p2.y *s,
				p1.z * (1d-s) + p2.z *s
			), origin, axis, s*Math.PI/2);
			toRet.addPoint(newPoint);
		}
		return toRet;
	}
}
