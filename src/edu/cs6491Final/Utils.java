package edu.cs6491Final;

import java.util.ArrayList;
import java.util.List;

public final class Utils {
	public static double appHeight;
	public static Point g_center = new Point(0,0,0);
//	public static PApplet applet;
	
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
		} else if (a instanceof SplineAxis) {
			for(int i = 0; i < loop.points.size(); i++) {
				//normal/binorm/tan
				SplineAxis axis = ((SplineAxis) a);
				double percentage = loop.GetPercentage(i);
				//System.out.println("find norm");
				Vector N = axis.getN(percentage);
				//System.out.println("N = " + N.toString());
				//System.out.println("find binorm");
				Vector H = axis.getB(percentage);
				//System.out.println("H = " + H.toString());
				//System.out.println("find tangent");
				Vector T = axis.getT(percentage);
				//T = T.normalize();
				//System.out.println("T = " + (T.toString()));

				Point p = loop.points.get(i);
				Point A = axis.getPointFromPercentage(percentage);

				Vector AP = new Vector(p.x-A.x, p.y-A.y, p.z-A.z);
				//System.out.println("AP = " + AP.toString());
				//AP.normalize();

				Point op = loop.origPt.get(i);
				if(op.x == -1 && op.y == -1 && op.z == -1) {
					op.x = AP.dotProduct(T);
					op.y = AP.dotProduct(H);
					op.z = AP.dotProduct(N);
				}

				//P= B+xT+yH+zN
				Vector Tx = new Vector(T.x*op.x, T.y*op.x, T.z*op.x);
				Vector Hy = new Vector(H.x*op.y, H.y*op.y, H.z*op.y);
				Vector Nz = new Vector(N.x*op.z, N.y*op.z, N.z*op.z);

//				axis.DrawTHN(A, Tx, Hy, Nz);

				//System.out.println(":::::::::::::::A before = " + A.toString());
				//System.out.println("p:" + p.toString());
				Point finalP = ((A.add(Tx)).add(Hy)).add(Nz);
				//System.out.println("final p: " +finalP.toString());

				toRet.addPoint(finalP);

				/*if (percentage > 0) {
					System.out.println("----------------");
					System.out.println((percentage*100) + "%");
					System.out.println("N: " + N.toString());
					System.out.println("T: " + T.toString());
					System.out.println("H: " + H.toString());
				}*/

				//A.Draw(applet);
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
				if (p.equals(q)) {
					line.add(p);
					break;
				}
				if (p.pointEquals(q)) {
					line.add(new GeneratedPoint(p.r*(1-t)+q.r*t, p));
					break;
				}
				Vector normal_q = q.to(l2_points.get(j+1)).normalize().rotate(3*Math.PI/2, new Vector(0,0,1));
				Point m = intersectionBetweenLines(p, p.add(normal_p), q, q.add(normal_q));
				if (Math.abs(p.distanceTo(m)-q.distanceTo(m)) < 1) {
					BallMorphData d = new BallMorphData(p,q,m);
					double r = d.getRadius(t);
					line.add(new GeneratedPoint(r, d.pointAlongBezier(t)));
					break;
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

	public static PolyLoop ballMorphLerp(Axis axis, SplineLine sl1, SplineLine sl2, double t) {
		SplineLine unmirrored = new SplineLine();
		for (SplineLine.ControlPoint cp : sl2.pts) {
			Point pv = mirrored(cp, axis);
			unmirrored.addPoint(pv, cp.r);
		}
		//Let's check equality here.
		boolean equal = true;
		for (int i = 0; i < sl1.pts.size(); i++) {
			equal &= sl1.pts.get(i).equals(unmirrored.pts.get(i));
		}
		CustomLine ballMorph;
		if (equal) {
			ballMorph = new CustomLine();
			ballMorph.addAll(sl1.calculateQuinticSpline());
		} else {
			ballMorph = ballMorphInterpolation(sl1, unmirrored,t);
		}
		CustomLine rot = new CustomLine();
		for (GeneratedPoint p : ballMorph) {
			Point newPoint = rotate(p, axis, Math.PI * t);
			rot.add(new GeneratedPoint(p.r, newPoint));
		}
		return rot.getBoundingLoop();
	}
}
