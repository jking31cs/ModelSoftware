package edu.cs6491Final;

import java.util.ArrayList;

import processing.core.PApplet;

public class Stroke implements Drawable {
	
	public ArrayList<Point> points;
	public ArrayList<Float> radiusAtPoint;
	
	public Stroke(){
		points = new ArrayList<Point>();
		radiusAtPoint = new ArrayList<Float>();
	}
	
	public void addPoint(Point p, Float r){
		points.add(p);
		radiusAtPoint.add(r);
	}
	
	public void refine(){
		int originalSize=points.size();
		for(int ptIndex=0;ptIndex<originalSize-1;ptIndex++){
			Point p1=points.get(ptIndex);
			Point p2=points.get(ptIndex+1);
			Point avg=new Point(
									p1.asVec().add(p2.asVec()).x/2, 
									p1.asVec().add(p2.asVec()).y/2, 
									p1.asVec().add(p2.asVec()).z/2
								);
			points.add(ptIndex+1, avg);
		}
		
	}

	@Override
	public void draw(PApplet p) {
		for(int ptIndex=0;ptIndex<points.size();ptIndex++){
			Point pt=points.get(ptIndex);
			pt.draw(p);
			p.noFill();
			p.strokeWeight(3);
			p.stroke(255,0,0);
			p.ellipse((float) pt.x, (float) pt.y, radiusAtPoint.get(ptIndex), radiusAtPoint.get(ptIndex));
			p.fill(0);
		}
		for(int ptIndex=0;ptIndex<points.size()-1;ptIndex++){
			Point p1=points.get(ptIndex);
			Point p2=points.get(ptIndex+1);
			p.line(
					(float) p1.x, (float) p1.y, (float) p1.z, 
					(float) p2.x, (float) p2.y, (float) p2.z
			);
		}
	}
}
