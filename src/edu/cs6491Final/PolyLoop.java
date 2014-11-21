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
	
	public PolyLoop() {
		this.points = new ArrayList<>();
	}
	
	public void addPoint(Point p) {
		points.add(p);
	}

	public double GetPercentage(int i){
		double percentage = (points.get(i).y/(Utils.appHeight));
		return percentage;
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

}
