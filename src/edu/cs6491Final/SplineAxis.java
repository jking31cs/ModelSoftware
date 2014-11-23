package edu.cs6491Final;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import processing.core.PApplet;

// TO DO:
// get tangents working under the assumption that normals are just 90 degree rotations
// check extrusion is working
// get 3d manipulation of control points working
// fix the normals so that they are actually being advected along the curve
// ask Jarek why the fuck we usin quintic?

public class SplineAxis extends Axis {
	public List<Point> controlPoints = new ArrayList<>();
	private List<Point> splinePoints = new ArrayList<>();
	PApplet pApp;
	PolyLoop l1, l2;

	public SplineAxis(Point origin, List<Point> controlPoints, PApplet p, PolyLoop _l1, PolyLoop _l2) {
		super(origin);
		l1 = _l1;
		l2 = _l2;
		pApp = p;
		this.controlPoints = controlPoints;
		this.splinePoints = controlPoints;

		System.out.print("control points: ");
		for(Point pnt:controlPoints){            
            System.out.print(pnt.toString() + "; ");
        }
        System.out.print("\n");

        quintic();
	}
//	v*(splinePoints.size()-1)

	public void UpdateLoopRefs(PolyLoop _l1, PolyLoop _l2){
		l1 = _l1;
		l2 = _l2;
	}

	public Point GetFromB(double percentage){
		Point point = splinePoints.get(getIndexFromPercentage(percentage));
		return point;
	}

	public void quintic() {
		List<Point> newSpline = controlPoints;
		for(int lmno = 0; lmno < 4; lmno++) {
			// refine once
			newSpline = refine(newSpline);
			// dual four times
			for (int i = 0; i < 2; i++) {
				newSpline = dual(newSpline);
			}
		}

		splinePoints = newSpline;
	}

	public List<Point> refine(Point[] points) {
		return refine(Arrays.asList(points));
	}


	public List<Point> refine(List<Point> points) {
		// insert new points at midpoints
		List<Point> output = new ArrayList<>();

		Point p1 = origin, p2 = origin;
		for (int i = 0; i < points.size()-1; i++) {
			p1 = points.get(i);
			p2 = points.get(i+1);

			double mx = (p1.x + p2.x)/2;
			double my = (p1.y + p2.y)/2;
			double mz = (p1.z + p2.z)/2;
			Point m = new Point(mx, my, mz);
			output.add(p1);
			output.add(m);
		}
		output.add(p2);

		return output;
	}

	public List<Point> dual(List<Point> points) {
		// new points are the midpoints
		List<Point> output = new ArrayList<>();

		Point p1 = origin;
		Point p2 = origin;
		for (int i = 0; i < points.size()-1; i++) {
			p1 = points.get(i);
			p2 = points.get(i+1);

			double mx = (p1.x + p2.x)/2;
			double my = (p1.y + p2.y)/2;
			double mz = (p1.z + p2.z)/2;
			Point m = new Point(mx, my, mz);
			output.add(m);
		}

		return output;
	}

	public int getIndexFromPercentage(double percentage) {
		Double length = GetTotalSplineLength();
		length *= percentage;
		//percentage *= (splinePoints.size()-1);
		int index = GetIndexFromLength(length);//Double.valueOf(percentage).intValue();
		return index;
	}

	public int GetIndexFromLength(double l){
		double traversedDist = 0d;
		int index = 0;
		for(int i = 0; i < splinePoints.size()-2; i++){
			Point firstP = splinePoints.get(i);
			Point nextP = splinePoints.get(i+1);
			double add = Math.sqrt(Math.pow((nextP.x-firstP.x), 2) + Math.pow((nextP.y-firstP.y), 2) + Math.pow((nextP.z-firstP.z), 2));
			if((traversedDist += add) > l){
				index = i;
				break;
			}
			traversedDist += add;
		}

		return index;

	}

	public double GetTotalSplineLength(){
		double totalDist = 0d;
		for(int i = 0; i < splinePoints.size()-2; i++){
			Point firstP = splinePoints.get(i);
			Point nextP = splinePoints.get(i+1);
			double add = Math.sqrt(Math.pow((nextP.x-firstP.x), 2) + Math.pow((nextP.y-firstP.y), 2) + Math.pow((nextP.z-firstP.z), 2));
			totalDist += add;
		}
		return totalDist;
	}

	public Point getPointFromPercentage(double percentage) {
		//System.out.println("LDAKFJIEWFAEIO***==== getPointFromPercentage " + percentage);
		int index = getIndexFromPercentage(percentage);
		return getPointAtIndex(index);
	}

	public Vector getN(double percentage){
		//System.out.println("N percent = " + percentage);
		int index = getIndexFromPercentage(percentage);
		Point a = getPointAtIndex(index-1);
		Point b = getPointAtIndex(index);
		Point c = getPointAtIndex(index+1);

		Vector BA = new Vector(a.x-b.x, a.y-b.y, a.z-b.z);
		Vector BC = new Vector(c.x-b.x, c.y-b.y, c.z-b.z);

		//System.out.println("BA : " + BA.toString());
		//System.out.println("BC : " + BC.toString());
		Vector cross = (BA.crossProd(BC));
		if (Double.isNaN(cross.x) || Double.isNaN(cross.y) || Double.isNaN(cross.z)) {
			return (new Vector(0,0,1));
		} else if (cross.x == 0 && cross.y == 0 && cross.z == 0) {
			return (new Vector(0,0,1));
		} else {
			return cross.normalize();
		}
			
	}

	public Vector getT(double percentage) {
		//System.out.println("T percent = " + percentage);
		int index = getIndexFromPercentage(percentage);
		Point a = getPointAtIndex(index-1);
		Point b = getPointAtIndex(index+1);

		Vector AB = new Vector(b.x-a.x, b.y-a.y, b.z-a.z);
		AB = AB.normalize();
		//AB.draw(pApp, a);
		return AB;
	}

	public Vector getB(double percentage) {
		//System.out.println("B percent = " + percentage);
		int index = getIndexFromPercentage(percentage);
		Vector norm = getN(percentage);
		Point a = getPointAtIndex(index);
		//Point b = getPointAtIndex(index+1);
		//System.out.println("+++++++++++a and b:" + index + ", " + (index+1) + ", " + percentage);

		//Vector BA = new Vector(a.x-b.x, a.y-b.y, a.z-b.z);
		Vector BA = getT(percentage);
		BA = (BA.crossProd(norm));
		pApp.stroke(0,255,0);
		BA.draw(pApp, a);
		BA = BA.normalize();
		//a.draw(pApp);
		return BA;
	}

	public Point getPointAtIndex(int index) {
		Point b = origin;
		/*while (index > (splinePoints.size()-1)) {
			index -= (splinePoints.size()-1);
		}
		while (index < 0) {
			index += (splinePoints.size()-1);
		}*/

		/*if (index > (splinePoints.size()-1)) {
			index =  (splinePoints.size()-1);
		} else if (index < 0) {
			index = 0;
		}*/

		b = splinePoints.get(index);
		return b;
	}

	@Override
	public Point closestProject(Point p) {
		Axis tmpStraight = new StraightAxis(new Point(pApp.width/2, pApp.height, 0),new Vector(0,-1,0));
		Point tmpPt = tmpStraight.closestProject(p);

		return origin;
	}

	@Override
	public Vector tangentVectorAt(Point p) {
		//Vector p_to_c = p.to(center);
		//Vector rotV = new Vector(0,0,-1); //assuming we're on XY plane.
		return new Vector(0,-1,0);
	}

	@Override
	public void draw(PApplet p) {
		//This only holds true when circule is in XY plane
		p.pushMatrix();
		//this.origin.draw(p);
		p.noFill();
		Point p2 = origin;
		for (int i = 0; i < splinePoints.size()-1; i++) {
			Point p1 = splinePoints.get(i);
			//System.out.println("draw spline points " + p1.toString());
			p2 = splinePoints.get(i + 1);	
			//p1.draw(p);
			p.line(
				(float) p1.x, (float) p1.y, (float) p1.z,
				(float) p2.x, (float) p2.y, (float) p2.z
			);
		}
		//p2.draw(p);

		p.stroke(100, 100, 50);
		for (int i = 0; i < controlPoints.size()-1; i++) {
			Point p1 = controlPoints.get(i);
			p2 = controlPoints.get(i + 1);	
			p1.draw(p);
			p.line(
				(float) p1.x, (float) p1.y, (float) p1.z,
				(float) p2.x, (float) p2.y, (float) p2.z
			);
		}

		p2.draw(p);

		p.stroke(50, 50, 0);
		//DrawProjection(p);
		p.popMatrix();

	}

	public void DrawProjection(PApplet p){
		for(int i = 0; i < l1.points.size(); i++){
			double percentage = l1.GetPercentage(i);
			getPointFromPercentage(percentage).draw(p);
			//System.out.println(getIndexFromPercentage(percentage));
		}
		p.stroke(255, 0, 0);
		for(int j = 0; j < l2.points.size(); j++){
			double percentage = l2.GetPercentage(j);
			getPointFromPercentage(percentage).draw(p);
		}
	}
}
