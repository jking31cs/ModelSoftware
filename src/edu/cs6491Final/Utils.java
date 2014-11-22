package edu.cs6491Final;

import java.util.List;

public final class Utils {
	
	/**
	 * Mirrors a point around an axis by rotating the vector formed from the origin to the point PI radians
	 * @param p Point to mirror
	 * @param origin local origin
	 * @param axis axis to rotate around
	 * @return new Point that is rotated around PI radians
	 */
	public static Point mirrored(Point p, Axis a) {
		
		return rotate(p,a,Math.PI);
	}

	public static Point rotate(Point p, Axis a, double theta) {
		Point closestP = a.closestProject(p);
		Vector v = new Vector(p.x - closestP.x, p.y - closestP.y, p.z - closestP.z);
		Vector rotV = a.tangentVectorAt(closestP);
		Vector rotated = v.rotate(theta, rotV.normalize());
		return new Point(rotated.x + closestP.x, rotated.y + closestP.y, rotated.z + closestP.z);
		
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
	public static PolyLoop lerp(Axis a, PolyLoop l1, PolyLoop l2, double s) {
		PolyLoop toRet = new PolyLoop();
		for (int i = 0; i < l1.points.size(); i++) {
			Point p1 = l1.points.get(i);
			Point p2 = mirrored(l2.points.get(i),a);
			Point newPoint = rotate(new Point(
				(1-s)*p1.x + s*p2.x,
				(1-s)*p1.y + s*p2.y,
				(1-s)*p1.z + s*p2.z
			), a, s*Math.PI);
			toRet.addPoint(newPoint);
		}
		return toRet;
	}
	
	/**
	 * Morph's a polyloop based on the axis type, assumes initial axis is a straight line axis.
	 * @param axis
	 * @param polyloop
	 * @return morphedPolyloop
	 */
	public static PolyLoop morphAboutAxis(Axis a, PolyLoop loop){
		PolyLoop toRet = new PolyLoop();
		if (a instanceof StraightAxis){
			toRet=loop;
		} else if (a instanceof CircularAxis) {
			CircularAxis ca = (CircularAxis) a;
			for(Point p : loop.points) {
				Vector r = new Vector(p.x - a.origin.x, 0, 0);
				Vector z = new Vector(0, p.y - a.origin.y, 0);
				double zMag = z.getMag();
				double alpha = zMag / ca.radius;
				Vector radiusVec = ca.center.to(a.origin);
				Vector radiusRot = radiusVec.rotate(alpha, new Vector(0,0,1));
				Vector rRot = r.rotate(alpha, new Vector(0,0,1));
				Vector morphPos = ca.center.asVec().add(radiusRot).add(rRot);
				toRet.addPoint(new Point(morphPos.x, morphPos.y, morphPos.z));
				
			}
		}
		return toRet;
	}

	/**
	 * This assumes that p is on l1, find the q on l2 and the center point
	 * @param p
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static BallMorphData getBallMorphDataAt(Point p, SplineLine l1, SplineLine l2) {

		//First get out the normal vector at p in l1.
		Vector normal_at_p = l1.getNormalAtPoint(p);
		Point m1 = p.add(normal_at_p); //For line intersection.

		boolean found = false;
		List<SplineLine.GeneratedPoint> points = l2.calculateQuinticSpline();
		Integer min = 0;
		Integer max = points.size();
		Integer qIndex = points.size()/2;
		Point center = null;
		while (!found) {
			Point q = points.get(qIndex).pt;
			Vector normal_at_q = l2.getNormalAtPoint(q);
			Point m2 = q.add(normal_at_q);
			Point m = intersectionBetweenLines(p, m1, q, m2);

			if (Math.abs(p.distanceTo(m) - q.distanceTo(m)) < 1) {
				center = m;
				found = true;
			} else if (p.distanceTo(m) > q.distanceTo(m)) {
				Integer temp = qIndex;
				qIndex = (qIndex + min) / 2;
				max = temp;
			} else {
				Integer temp = qIndex;
				qIndex = (qIndex + max) / 2;
				min = temp;
			}

			if (max-min < 2) {
				center = m;
				found = true;
			}
		}


		return new BallMorphData(p,points.get(qIndex).pt,center);
	}

	public static Point intersectionBetweenLines(Point p1,Point p2,Point p3,Point p4) {
		double xTop = ((p1.x*p2.y - p1.y*p2.x)*(p3.x-p4.x)) - ((p1.x - p2.x)*(p3.x*p4.y - p3.y*p4.x));
		double xBot = ((p1.x - p2.x)*(p3.y-p4.y))-((p1.y-p2.y)*(p3.x-p4.x));
		double yTop = ((p1.x*p2.y - p1.y*p2.x)*(p3.y-p4.y)) - ((p1.y - p2.y)*(p3.x*p4.y - p3.y*p4.x));
		double yBot = ((p1.x - p2.x)*(p3.y-p4.y))-((p1.y-p2.y)*(p3.x-p4.x));

		return new Point(xTop/xBot, yTop/yBot, 0);
	}
}
