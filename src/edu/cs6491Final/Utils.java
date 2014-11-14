package edu.cs6491Final;

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

	public static BezierLine morphAboutAxis(Axis a, BezierLine loop){
		BezierLine toRet = new BezierLine();
		if (a instanceof StraightAxis){
			toRet=loop;
		} else if (a instanceof CircularAxis) {
			CircularAxis ca = (CircularAxis) a;
			for(BezierLine.ControlPoint p : loop.pts) {
				Vector r = new Vector(p.pt.x - a.origin.x, 0, 0);
				Vector z = new Vector(0, p.pt.y - a.origin.y, 0);
				double zMag = z.getMag();
				double alpha = zMag / ca.radius;
				Vector radiusVec = ca.center.to(a.origin);
				Vector radiusRot = radiusVec.rotate(alpha, new Vector(0,0,1));
				Vector rRot = r.rotate(alpha, new Vector(0,0,1));
				Vector morphPos = ca.center.asVec().add(radiusRot).add(rRot);
				toRet.addPoint(new Point(morphPos.x, morphPos.y, morphPos.z), p.r);

			}
		}
		return toRet;
	}

	public static BezierLine lerp(Axis axis, BezierLine l1, BezierLine l2, double s) {
		BezierLine toRet = new BezierLine();
		//The idea is the same, just do it with control points, then we can calculate curve later.
		for (int i = 0; i < l1.pts.size(); i++) {
			BezierLine.ControlPoint p1 = l1.pts.get(i);
			BezierLine.ControlPoint p2 = new BezierLine.ControlPoint(l2.pts.get(i).r, mirrored(l2.pts.get(i).pt, axis));
			Point newPoint = rotate(new Point(
				(1-s)*p1.pt.x + s*p2.pt.x,
				(1-s)*p1.pt.y + s*p2.pt.y,
				(1-s)*p1.pt.z + s*p2.pt.z
			), axis, s*Math.PI);
			double newR = (1-s)*p1.r + s*p2.r;
			toRet.addPoint(newPoint, newR);
		}
		return toRet;
	}
}
