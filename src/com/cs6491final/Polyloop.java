package com.cs6491final;

import java.util.ArrayList;

import processing.core.PApplet;

public class Polyloop {
	public ArrayList<Point> points;
	
	private PApplet p;
	
	public Polyloop(PApplet p){
		points=new ArrayList<Point>();
		this.p=p;
	}
	
	public Polyloop(ArrayList<Point> points, PApplet p){
		this.points=points;
		this.p=p;
	}
	
	public void addPoint(Point pt){
		points.add(pt);
	}
	
	public void deletePoint(Point pt){
		points.remove(pt);
	}
	
	public void deletePoint(Integer ptIndex){
		points.remove(ptIndex);
	}
	
	public void drawPolyloop2D(){
		for(int ptIndex=0;ptIndex<points.size();ptIndex++){
			p.stroke(0);
			p.line((float)points.get(ptIndex).x, (float)points.get(ptIndex).y,(float) points.get((ptIndex+1)%points.size()).x, (float)points.get((ptIndex+1)%points.size()).y);
			points.get(ptIndex).drawPoint2D();	
			p.text(ptIndex, (float)points.get(ptIndex).x-4, (float)points.get(ptIndex).y+4);
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
		Polyloop other = (Polyloop) obj;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Polyloop [points=" + points + "]";
	}

}
