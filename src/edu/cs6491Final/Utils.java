package edu.cs6491Final;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {
	
	/**
	 * Mirrors a point around an axis by rotating the vector formed from the origin to the point PI radians
	 * @param p Point to mirror
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

	public static CustomLine ballMorphInterpolation(SplineLine l1, SplineLine l2, double t) {
		List<BallMorphData> data = l1.calculateQuinticSpline().stream().map(
			p -> getBallMorphDataAt(p, l1, l2)).collect(Collectors.toList());
		CustomLine line = new CustomLine();
		for (BallMorphData d : data) {
			double r = (1-t)*d.p.r + t*d.q.r;
			line.add(new GeneratedPoint(r, d.pointAlongBezier(t)));
		}
		return line;
	}

	public static CustomLine bezierMorphInterpolation(SplineLine l1, SplineLine l2, double t) {
		List<BallMorphData> data = new ArrayList<>();
		List<GeneratedPoint> p_points = l1.calculateQuinticSpline();
		List<GeneratedPoint> q_points = l2.calculateQuinticSpline();

		if (p_points.size() != q_points.size()) throw new RuntimeException("Points must be the same size");

		for (int i = 0; i < p_points.size(); i++) {
			GeneratedPoint p = p_points.get(i);
			GeneratedPoint q = q_points.get(i);
			Vector p_normal = l1.getNormalAtPoint(p);
			Vector q_normal = l2.getNormalAtPoint(q);
			Point m = intersectionBetweenLines(p, p.add(p_normal), q, q.add(q_normal));
			data.add(new BallMorphData(p,q,m));
		}
		CustomLine line = new CustomLine();
		for (BallMorphData d : data) {
			double radius = d.getRadius(t);
			Point point = d.pointAlongBezier(t);
			line.add(new GeneratedPoint(radius, point));
		}
		return line;
	}


	/**
	 * This assumes that p is on l1, find the q on l2 and the center point
	 */
	public static BallMorphData getBallMorphDataAt(GeneratedPoint p, SplineLine l1, SplineLine l2) {

		//First get out the normal vector at p in l1.
		Vector normal_at_p = l1.getNormalAtPoint(p);
		Point m1 = p.add(normal_at_p); //For line intersection.

		boolean found = false;
		List<GeneratedPoint> points = l2.calculateQuinticSpline();

		//let's refine points some more here for better options.
		refinePoints(points);
		refinePoints(points);
		double minDiff = Double.MAX_VALUE;
		Point center = null;
		GeneratedPoint q = null;
		for (GeneratedPoint gp : points) {
			GeneratedPoint temp = gp;
			Vector normal_at_temp = l2.getNormalAtPoint(temp);
			Point m2 = temp.add(normal_at_temp);

			Point m = intersectionBetweenLines(p,m1,temp,m2);

			double diff = Math.abs(p.distanceTo(m) - temp.distanceTo(m));
			if (diff < minDiff) {
				minDiff = diff;
				center = m;
				q = temp;
			}
		}
		return new BallMorphData(p,q,center);
	}

	private static void refinePoints(List<GeneratedPoint> points) {
		List<GeneratedPoint> newPoints = new ArrayList<>();
		for (int i = 0; i < points.size()-1; i++) {
			GeneratedPoint p1 = points.get(i);
			GeneratedPoint p2 = points.get(i + 1);
			newPoints.add(getMidPoint(p1,p2));
		}
		int addIndex = 1;
		for (GeneratedPoint pt : newPoints) {
			points.add(addIndex, pt);
			addIndex += 2;
		}
	}

	private static GeneratedPoint getMidPoint(GeneratedPoint p1, GeneratedPoint p2) {
		Point p = p1.add(p1.to(p2).mul(.5));
		double r = (p1.r + p2.r) / 2;
		return new GeneratedPoint(r,p);
	}

	public static Point intersectionBetweenLines(Point p1,Point p2,Point p3,Point p4) {
		double xTop = ((p1.x*p2.y - p1.y*p2.x)*(p3.x-p4.x)) - ((p1.x - p2.x)*(p3.x*p4.y - p3.y*p4.x));
		double xBot = ((p1.x - p2.x)*(p3.y-p4.y))-((p1.y-p2.y)*(p3.x-p4.x));
		double yTop = ((p1.x*p2.y - p1.y*p2.x)*(p3.y-p4.y)) - ((p1.y - p2.y)*(p3.x*p4.y - p3.y*p4.x));
		double yBot = ((p1.x - p2.x)*(p3.y-p4.y))-((p1.y-p2.y)*(p3.x-p4.x));

		return new Point(xTop/xBot, yTop/yBot, 0);
	}
}
