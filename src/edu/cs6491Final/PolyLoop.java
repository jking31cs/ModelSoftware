package edu.cs6491Final;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * PolyLoop object that just holds a list of points in order.
 * @author jking31
 *
 */
public class PolyLoop implements Drawable {
	
	public List<Point> points;
	public List<Point> origPt;
	int pickedVert;
	
	public PolyLoop() {
		this.points = new ArrayList<>();
		this.origPt = new ArrayList<>();
	}
	
	public void addPoint(Point p) {
		points.add(p);
		origPt.add(new Point(-1, -1, -1));
	}

	public double GetPercentage(int i){
		//System.out.println("////////// Y = " + points.get(i).y + ", height = " + Utils.appHeight);
		double percentage = (points.get(i).y/(Utils.appHeight));
		return percentage;
	}

	public double area() {
		double area = 0d;

		for (int i = 0; i < points.size()-1; i++) {
			Vector v0 = points.get(i).asVec();
			Vector v1 = points.get((i+1) % points.size()).asVec();
			area += (v0.crossProd(v1).getMag())/2d;
		}

		return area/points.size();
	}

	public Point COM() {
		Point com = new Point(0,0,0);

		for (int i = 0; i < points.size()-1; i++) {
			com.x += points.get(i).x / points.size();
			com.y += points.get(i).y / points.size();
			com.z += points.get(i).z / points.size();
		}

		return com;
	}

	// public ArrayList<Point> GetBList() {
	// 	List<Point> blist = new ArrayList<>();
	// 	for(Point pt : points) {

	// 	}
	// }

	/**
	 * This will draw a line between each point in order of the points listed to form a simple loop.
	 */
	@Override
	public void draw(PApplet p) {
		for (Point pt : points) {
			pt.draw(p);
		}
		for (int i = 0; i < points.size(); i++) {
			Point p1 = points.get(i);
			Point p2 = points.get((i + 1) % points.size());			
			p.line(
				(float) p1.x, (float) p1.y, (float) p1.z,
				(float) p2.x, (float) p2.y, (float) p2.z
			);
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolyLoop other = (PolyLoop) obj;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PolyLoop [points=" + points + "]";
	}

	public Point showPicked() {
		Point pt = points.get(pickedVert);
		return pt;
	}

	//pick stuff
	public int idOfVertexWithClosestScreenProjectionTo(Point M) { // for picking a vertex with the mouse
	    if ( Utils.g_center != null)
	    {
	      pickedVert=0; 
	      for (int i=1; i< points.size(); i++) 
	      {
	        if (Utils.g_center.distanceTo(points.get(i)) <= Utils.g_center.distanceTo(points.get(i)))
	        {
	          pickedVert=i;
	        }
	      }
	    }
//	    pv = pickedVert;
	    return pickedVert;
  	}

}
