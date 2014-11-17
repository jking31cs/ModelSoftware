package edu.cs6491Final;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

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
	    	
	    	for(int subDivisionCtr=0; subDivisionCtr<10;subDivisionCtr++)
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
	    		
	    		//Untuck the curve
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

	    @Override
	    public void draw(PApplet p) {
	        for (ControlPoint pt : pts) pt.draw(p);



	        if (pts.size() > 1) {
	            List<GeneratedPoint> generatedPoints = calculateQuinticSpline();
	            for (int i = 1; i < generatedPoints.size(); i++) {
	                Point prev = generatedPoints.get(i-1).pt;
	                Point cur = generatedPoints.get(i).pt;
	                p.stroke(255, 0, 0);
	                p.strokeWeight((float) generatedPoints.get(i-1).r);
	                p.line(
	                    (float) prev.x,(float) prev.y,(float) prev.z,
	                    (float) cur.x, (float) cur.y, (float) cur.z
	                );
	            }
	        }

	    }
}
