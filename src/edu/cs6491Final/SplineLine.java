package edu.cs6491Final;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SplineLine implements Drawable{
	 public static class ControlPoint implements Drawable {

	        public double r;
	        public Point pt;

	        public ControlPoint(double r, Point p) {
	            this.r = r;
	            this.pt = p;
	        }

	        @Override
	        public void draw(PApplet p) {
	            p.stroke(0,255,0);
	            p.fill(0, 255, 0);
	            p.ellipse((float) pt.x, (float) pt.y, (float) r, (float) r);
	        }
	    }

	    private static class GeneratedPoint extends ControlPoint {
	        public GeneratedPoint(double r, Point p) {
	            super(r, p);
	        }
	    }

	    public List<ControlPoint> pts;

	    public SplineLine() {
	        this.pts = new ArrayList<>();
	    }

	    public void addPoint(Point p, double r) {
	        this.pts.add(new ControlPoint(r, p));
	    }

	    public List<GeneratedPoint> calculateQuinticSpline(){
	    	List<GeneratedPoint> toRet = new ArrayList<>();
	    	
	    	List<GeneratedPoint> newControlPoints = new ArrayList<>();
	    	for(ControlPoint cp : pts){
	    		newControlPoints.add(new GeneratedPoint(cp.r, cp.pt));
	    	}
	    	
	    	for(int subDivisionCtr=0; subDivisionCtr<5;subDivisionCtr++)
	    	{
	    		//Refine the curve
	    		List<GeneratedPoint> refinedCurve = new ArrayList<>();
	    		for(int ptIndex = 0; ptIndex < newControlPoints.size() - 1; ptIndex++){
	    			ControlPoint p1=newControlPoints.get(ptIndex);
	    			ControlPoint p2=newControlPoints.get(ptIndex + 1);
	    			Vector p1Vec=p1.pt.asVec();
	    			Vector p2Vec=p2.pt.asVec();
	    			Vector avgVec=p1Vec.add(p2Vec).mul(0.5);
	    			Point avgPoint=new Point(avgVec.x, avgVec.y, avgVec.z);
	    			double avgRadius=(p1.r+p2.r)/2;
	    			GeneratedPoint p1Gen=new GeneratedPoint(p1.r, p1.pt);
	    			GeneratedPoint avgGen=new GeneratedPoint(avgRadius, avgPoint);
	    			refinedCurve.add(p1Gen);
	    			refinedCurve.add(avgGen);
	    		}
	    		refinedCurve.add(new GeneratedPoint(pts.get(pts.size()-1).r, pts.get(pts.size()-1).pt));


	    		//Tuck the curve
	    		List<GeneratedPoint> tuckedCurve = new ArrayList<>();
	    		tuckedCurve.add(refinedCurve.get(0));
	    		for(int ptIndex = 1; ptIndex < refinedCurve.size() - 1; ptIndex++){
	    			GeneratedPoint p1=refinedCurve.get(ptIndex - 1);
	    			GeneratedPoint p=refinedCurve.get(ptIndex);
	    			GeneratedPoint p2=refinedCurve.get(ptIndex + 1);
	    			Vector p1Vec=p1.pt.asVec();
	    			Vector p2Vec=p2.pt.asVec();
	    			Vector pVec=p.pt.asVec();
	    			Vector avgVec=p1Vec.add(p2Vec).mul(0.5);
	    			Vector tuckVec=avgVec.sub(pVec).mul(0.5);
	    			GeneratedPoint tuckedPoint = new GeneratedPoint(
	    					(p1.r+p2.r)/2,
	    					new Point(p.pt.x+tuckVec.x, p.pt.y+tuckVec.y, p.pt.z+tuckVec.z));

	    			tuckedCurve.add(tuckedPoint);
	    		}
	    		tuckedCurve.add(refinedCurve.get(refinedCurve.size() - 1));
	    		
	    		//Tuck the curve again
	    		List<GeneratedPoint> unTuckedCurve = new ArrayList<>();
	    		unTuckedCurve.add(tuckedCurve.get(0));
	    		for(int ptIndex = 1; ptIndex < tuckedCurve.size() - 1; ptIndex++){
	    			GeneratedPoint p1=tuckedCurve.get(ptIndex - 1);
	    			GeneratedPoint p=tuckedCurve.get(ptIndex);
	    			GeneratedPoint p2=tuckedCurve.get(ptIndex + 1);
	    			Vector p1Vec=p1.pt.asVec();
	    			Vector p2Vec=p2.pt.asVec();
	    			Vector pVec=p.pt.asVec();
	    			Vector avgVec=p1Vec.add(p2Vec).mul(0.5);
	    			Vector tuckVec=avgVec.sub(pVec).mul(0.5);
	    			double deltaR=p.r - (p1.r+p2.r)/2;
	    			GeneratedPoint tuckedPoint = new GeneratedPoint(
	    					p.r +deltaR,
	    					new Point(p.pt.x+tuckVec.x, p.pt.y+tuckVec.y, p.pt.z+tuckVec.z));

	    			unTuckedCurve.add(tuckedPoint);
	    		}
	    		unTuckedCurve.add(tuckedCurve.get(tuckedCurve.size() - 1));
	    		newControlPoints=unTuckedCurve;
	    		toRet=unTuckedCurve;
	    	}
	    	return toRet;
	    }

	    public ControlPoint closestPointWithinRange(Point p, double distance) {
	        double minDistance = distance;
	        ControlPoint selected = null;
	        for (ControlPoint pt : pts) {
	            if (pt.pt.distanceTo(p) < minDistance) {
	                minDistance = pt.pt.distanceTo(p);
	                selected = pt;
	            }
	        }
	        return selected;
	    }
	    
	    public Vector getNormalAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){
	    	Vector toRet=new Vector(0,0,0);
	    	Vector toPrev=curr.pt.to(prev.pt).normalize();
	    	Vector fromPrev=prev.pt.to(curr.pt).normalize();
	    	Vector toNext=curr.pt.to(next.pt).normalize();
	    	Vector cross=fromPrev.crossProd(toNext);
	    	if(cross.z>0)
	    	{
	    		toRet=toPrev.add(toNext).normalize();
	    		toRet=toRet.mul(-1);
	    	}
	    	else
	    	{
	    		toRet=toPrev.add(toNext).normalize();
	    	}
	    	return toRet;	
	    }
	    public Vector getNormalAtPoint(GeneratedPoint curr, GeneratedPoint next){
	    	Vector toRet=new Vector(0,0,0);
	    	Vector toNext=curr.pt.to(next.pt).normalize();
	    	toRet=toNext.normalize().rotate(Math.PI/2, new Vector(0, 0, 1));
	    	return toRet;	
	    }

	    @Override
	    public void draw(PApplet p) {

	        if (pts.size() > 1) {
	        	List<GeneratedPoint> generatedPoints = calculateQuinticSpline();
	        	p.fill(255, 0, 0);
	        	p.stroke(255, 0, 0);
	        	p.beginShape();
	        	GeneratedPoint first=generatedPoints.get(0);
	        	GeneratedPoint second=generatedPoints.get(1);
	        	GeneratedPoint last=generatedPoints.get(generatedPoints.size()-1);
	        	GeneratedPoint secondLast=generatedPoints.get(generatedPoints.size()-2);
	        	Stack<Vector> offsets=new Stack<>();
	        	int ptCtr=0;
	        	if(pts.size()>2){   
	        		//Offset vertices on one side
	        		for (ptCtr = 1; ptCtr < generatedPoints.size() - 1; ptCtr++) {
	        			GeneratedPoint prev = generatedPoints.get(ptCtr-1);
	        			GeneratedPoint cur = generatedPoints.get(ptCtr);
	        			GeneratedPoint next = generatedPoints.get(ptCtr+1);
	        			Vector offset=getNormalAtPoint(prev,cur,next).mul(Math.abs(cur.r));
	        			offsets.push(offset);
	        			Vector offsetPoint=cur.pt.asVec().add(offset);
	        			p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
	        		}
	        	}
	        	
	        		//Draw semi-circle at end
	        		double deltaRot=Math.PI/20;
	        		Vector offsetAtEnd=getNormalAtPoint(last,secondLast).mul(last.r);
	        		for(int i=0; i < Math.PI/deltaRot + 1; i++){
	        			Vector offsetPoint=last.pt.asVec().add(offsetAtEnd);
	        			p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
	        			offsetAtEnd=offsetAtEnd.rotate(deltaRot, new Vector(0, 0, 1));
	        		}
	        	if(pts.size() >1)	{
	        		
	        		//Offset vertices on the other side
	        		while(ptCtr>1){
	        			GeneratedPoint cur = generatedPoints.get(ptCtr);
	        			Vector offset=offsets.pop().mul(-1);
	        			Vector offsetPoint=cur.pt.asVec().add(offset);
	        			p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
	        			ptCtr--;
	        		}
	        	}
	        		
	        		//Draw semi-circle at start
	        		Vector offsetAtStart=getNormalAtPoint(first,second).mul(first.r);
	        		for(int i=0; i < Math.PI/deltaRot +1; i++){
	        			Vector offsetPoint=first.pt.asVec().add(offsetAtStart);
	        			p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
	        			offsetAtStart=offsetAtStart.rotate(deltaRot, new Vector(0, 0, 1));
	        		}
	        	
	        	p.endShape(PConstants.CLOSE);
	        }
	        
	        for (ControlPoint pt : pts) pt.draw(p);

	    }
}
