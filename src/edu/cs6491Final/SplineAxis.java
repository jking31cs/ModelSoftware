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
	public List<Point> splinePoints = new ArrayList<>();
	public List<Integer> bList = new ArrayList();
	PApplet pApp;

	public SplineAxis(Point origin, List<Point> controlPoints, List<Double> _bList, PApplet p) {
		super(origin);
		pApp = p;
		this.controlPoints = controlPoints;
		this.splinePoints = controlPoints;

		System.out.print("control points: ");
		for(Point pnt:controlPoints){            
            System.out.print(pnt.toString() + "; ");
        }
        System.out.print("\n");

        quintic();

        for(Double v : _bList) {
			int index = (int)(v*(splinePoints.size()-1));
			bList.add(index);
		}
	}

	public void quintic() {
		List<Point> newSpline = controlPoints;
		for(int lmno = 0; lmno < 4; lmno++) {
			// refine once
			newSpline = refine(newSpline);
			// dual four times
			for (int i = 0; i < 4; i++) {
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

		//connect end to start
		/*p1 = points.get(0);
		double _mx = (p1.x + p2.x)/2;
		double _my = (p1.y + p2.y)/2;
		double _mz = (p1.z + p2.z)/2;
		Point _m = new Point(_mx, _my, _mz);
		output.add(_m);		*/

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

		//connect end to start
		/*p1 = points.get(0);
		double _mx = (p1.x + p2.x)/2;
		double _my = (p1.y + p2.y)/2;
		double _mz = (p1.z + p2.z)/2;
		Point _m = new Point(_mx, _my, _mz);
		output.add(_m);*/

		return output;
	}

	public Vector getN(int index){
		Point a = getPointAtIndex(index);
		Point b = getPointAtIndex(index+1);
		Point c = getPointAtIndex(index+2);

		Vector BA = new Vector(a.x-b.x, a.y-b.y, a.z-b.z);
		Vector BC = new Vector(c.x-b.x, c.y-b.y, c.z-b.z);
		
		return (BA.crossProd(BC)).normalize();
			
	}

	public Vector getT(int index){
		Point a = getPointAtIndex(index);
		Point b = getPointAtIndex(index+1);

		Vector AB = new Vector(b.x-a.x, b.y-a.y, b.z-a.z);
		return AB.normalize();
	}

	public Vector getB(int index){
		Vector norm = getN(index);
		Point a = getPointAtIndex(index);
		Point b = getPointAtIndex(index+1);

		Vector BA = new Vector(a.x-b.x, a.y-b.y, a.z-b.z);
		return (BA.crossProd(norm)).normalize();
	}

	public Point getPointAtIndex(int index){
		Point b = origin;
		if((index+1)>(splinePoints.size()-1)) {
			b = splinePoints.get(0);
		} else {
			b = splinePoints.get(index+1);
		}
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
			System.out.println("draw spline points " + p1.toString());
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
		System.out.println(bList.size());
		for(int bP : bList) {
			System.out.println(bP);
			Point b = splinePoints.get(bP);
			b.draw(p);
		}
		p.popMatrix();

	}
}
