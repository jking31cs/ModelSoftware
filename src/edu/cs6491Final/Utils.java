package edu.cs6491Final;

import java.util.ArrayList;
import java.util.Arrays;
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
		CustomLine l1_points = new CustomLine(){{this.addAll(l1.calculateQuinticSpline());}};
		CustomLine l2_points = new CustomLine(){{this.addAll(l2.calculateQuinticSpline());}};
		refinePoints(l2_points);
		refinePoints(l2_points);
		refinePoints(l2_points);
		refinePoints(l2_points);
		refinePoints(l2_points);
		refinePoints(l2_points);
		refinePoints(l2_points);
		refinePoints(l2_points);
		CustomLine line = new CustomLine();
		for (int i = 0; i < l1_points.size()-1; i++) {
			GeneratedPoint p = l1_points.get(i);
			Vector normal_p = p.to(l1_points.get(i+1)).normalize().rotate(Math.PI / 2, new Vector(0, 0, 1));
			for (int j = 0; j < l2_points.size()-1; j++) {
				GeneratedPoint q = l2_points.get(j);
				Vector normal_q = q.to(l2_points.get(j+1)).normalize().rotate(3*Math.PI/2, new Vector(0,0,1));
				Point m = intersectionBetweenLines(p, p.add(normal_p), q, q.add(normal_q));
				if (Math.abs(p.distanceTo(m)-q.distanceTo(m)) < 1) {
					BallMorphData d = new BallMorphData(p,q,m);
					double r = d.getRadius(t);
					line.add(new GeneratedPoint(r, d.pointAlongBezier(t)));
				}
			}
		}
		return line;
	}

	public static Point intersectionBetweenLines(Point p1,Point p2,Point p3,Point p4) {
		double xTop = ((p1.x*p2.y - p1.y*p2.x)*(p3.x-p4.x)) - ((p1.x - p2.x)*(p3.x*p4.y - p3.y*p4.x));
		double xBot = ((p1.x - p2.x)*(p3.y-p4.y))-((p1.y-p2.y)*(p3.x-p4.x));
		double yTop = ((p1.x*p2.y - p1.y*p2.x)*(p3.y-p4.y)) - ((p1.y - p2.y)*(p3.x*p4.y - p3.y*p4.x));
		double yBot = ((p1.x - p2.x)*(p3.y-p4.y))-((p1.y-p2.y)*(p3.x-p4.x));

		return new Point(xTop/xBot, yTop/yBot, 0);
	}

	public static CustomLine centerLine(CustomLine l1, CustomLine l2) {
		CustomLine line = new CustomLine();
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		refinePoints(l2);
		for (int i = 0; i < l1.size()-1; i++) {
			double minDiff = Double.MAX_VALUE;
			Point p = l1.get(i);
			Vector normal_p = p.to(l1.get(i+1)).normalize().rotate(Math.PI/2, new Vector(0,0,1));
			for (int j = 0; j < l2.size()-1; j++) {
				Point q = l2.get(j);
				Vector normal_q = q.to(l2.get(j+1)).normalize().rotate(3*Math.PI/2, new Vector(0,0,1));
				Point m = intersectionBetweenLines(p, p.add(normal_p), q, q.add(normal_q));
				if (Math.abs(p.distanceTo(m)-q.distanceTo(m)) < 1) {
					line.add(new GeneratedPoint(10,m));
					break;
				}
				if (Math.abs(p.distanceTo(m)-q.distanceTo(m)) < minDiff) {
					minDiff = Math.abs(p.distanceTo(m)-q.distanceTo(m));
				}
			}
		}

		return line;
	}

	public static void refinePoints(CustomLine l2) {
		List<GeneratedPoint> newPoints = new ArrayList<>();
		for (int i = 0; i < l2.size() - 1; i++) {
			GeneratedPoint p1 = l2.get(i);
			GeneratedPoint p2 = l2.get(i+1);
			newPoints.add(new GeneratedPoint((p1.r+p2.r)/2, p1.add(p1.to(p2).mul(.5))));
		}
		int addIndex = 1;
		for (GeneratedPoint p : newPoints) {
			l2.add(addIndex, p);
			addIndex +=2;
		}
	}
}
