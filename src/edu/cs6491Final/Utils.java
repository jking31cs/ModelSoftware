package edu.cs6491Final;

public final class Utils {
	public static double appHeight;
//	public static PApplet applet;
	
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
		} else if (a instanceof SplineAxis) {
			for(int i = 0; i < loop.points.size(); i++) {
				//normal/binorm/tan
				SplineAxis axis = ((SplineAxis) a);
				double percentage = loop.GetPercentage(i);
				System.out.println("find norm");
				Vector N = axis.getN(percentage);
				System.out.println("N = " + N.toString());
				System.out.println("find binorm");
				Vector H = axis.getB(percentage);
				System.out.println("H = " + H.toString());
				System.out.println("find tangent");
				Vector T = axis.getT(percentage);
				//T = T.normalize();
				System.out.println("T = " + (T.toString()));

				Point p = loop.points.get(i);
				Point A = axis.getPointFromPercentage(percentage);

				Vector AP = new Vector(p.x-A.x, p.y-A.y, p.z-A.z);
				System.out.println("AP = " + AP.toString());
				//AP.normalize();

				double x = AP.dotProduct(T);
				double y = AP.dotProduct(H);
				double z = AP.dotProduct(N);

				//P= B+xT+yH+zN
				Vector Tx = new Vector(T.x*x, T.y*x, T.z*x);
				Vector Hy = new Vector(H.x*y, H.y*y, H.z*y);
				Vector Nz = new Vector(N.x*z, N.y*z, N.z*z);

				System.out.println(":::::::::::::::A before = " + A.toString());
				Point finalP = ((A.add(Tx)).add(Hy)).add(Nz);

				toRet.addPoint(finalP);

				if (percentage > 0) {
					System.out.println("----------------");
					System.out.println((percentage*100) + "%");
					//System.out.println("N: " + N.toString());
					//System.out.println("T: " + T.toString());
					//System.out.println("H: " + H.toString());
					System.out.println(finalP.toString());
				}

				//A.Draw(applet);
			}

		}

		return toRet;
	}

}
