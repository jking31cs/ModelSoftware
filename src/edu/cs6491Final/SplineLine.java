package edu.cs6491Final;

import com.sun.tools.javac.jvm.Gen;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplineLine implements Drawable{
	public static class ControlPoint extends GeneratedPoint {

		public ControlPoint(double r, Point p) {
			super(r,p);
		}

		@Override
		public void draw(PApplet p) {
			p.stroke(0,255,0);
			p.fill(0, 255, 0);
			p.ellipse((float) x, (float) y, (float) r, (float) r);
		}
	}

	public List<ControlPoint> pts;
	public List<GeneratedPoint> genPts;
	
	
	public String offsetMode="RADIAL";
	public int subDivisions=3;
	public boolean fillCurve=false;
	
	private PolyLoop offsetLoop;
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
			newControlPoints.add(new ControlPoint(cp.r, cp));
		}

		for(int subDivisionCtr=0; subDivisionCtr<subDivisions;subDivisionCtr++)
		{
			//Refine the curve
			List<GeneratedPoint> refinedCurve = new ArrayList<>();
			for(int ptIndex = 0; ptIndex < newControlPoints.size() - 1; ptIndex++){
				GeneratedPoint p1=newControlPoints.get(ptIndex);
				GeneratedPoint p2=newControlPoints.get(ptIndex + 1);
				Vector p1Vec=p1.asVec();
				Vector p2Vec=p2.asVec();
				Vector avgVec=p1Vec.add(p2Vec).mul(0.5);
				Point avgPoint=new Point(avgVec.x, avgVec.y, avgVec.z);
				double avgRadius=(p1.r+p2.r)/2;
				GeneratedPoint p1Gen=new GeneratedPoint(p1.r, p1);
				GeneratedPoint avgGen=new GeneratedPoint(avgRadius, avgPoint);
				refinedCurve.add(p1Gen);
				refinedCurve.add(avgGen);
			}
			refinedCurve.add(new GeneratedPoint(pts.get(pts.size()-1).r, pts.get(pts.size()-1)));


			//Tuck the curve
			List<GeneratedPoint> tuckedCurve = new ArrayList<>();
			tuckedCurve.add(refinedCurve.get(0));
			for(int ptIndex = 1; ptIndex < refinedCurve.size() - 1; ptIndex++){
				GeneratedPoint p1=refinedCurve.get(ptIndex - 1);
				GeneratedPoint p=refinedCurve.get(ptIndex);
				GeneratedPoint p2=refinedCurve.get(ptIndex + 1);
				Vector p1Vec=p1.asVec();
				Vector p2Vec=p2.asVec();
				Vector pVec=p.asVec();
				Vector avgVec=p1Vec.add(p2Vec).mul(0.5);
				Vector tuckVec=avgVec.sub(pVec).mul(0.5);
				GeneratedPoint tuckedPoint = new GeneratedPoint(
						(p1.r+p2.r)/2,
						new Point(p.x+tuckVec.x, p.y+tuckVec.y, p.z+tuckVec.z));

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
				Vector p1Vec=p1.asVec();
				Vector p2Vec=p2.asVec();
				Vector pVec=p.asVec();
				Vector avgVec=p1Vec.add(p2Vec).mul(0.5);
				Vector tuckVec=avgVec.sub(pVec).mul(0.5);
				double deltaR=p.r - (p1.r+p2.r)/2;
				GeneratedPoint tuckedPoint = new GeneratedPoint(
						p.r +deltaR,
						new Point(p.x+tuckVec.x, p.y+tuckVec.y, p.z+tuckVec.z));

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
			if (pt.distanceTo(p) < minDistance) {
				minDistance = pt.distanceTo(p);
				selected = pt;
			}
		}
		return selected;
	}

	public Vector getNormalOffsetAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){
		Vector norm=getNormalAtPoint(prev,curr, next).normalize();
		Vector tan=norm.rotate(Math.PI/2, getNormalToCurvePlane());
		double dprime1=(curr.r-prev.r)/(prev.to(curr).getMag());
		double dprime2=(next.r-curr.r)/(curr.to(next).getMag());
		double dprime=(dprime1+dprime2)/2;
		Vector nd1=tan.mul(-dprime);
		Vector nd=nd1.add(norm).mul(1/Math.sqrt(1+dprime*dprime));
		
		return nd.mul(curr.r/2);
	}

	public Vector getNormalAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){
		Vector toRet=new Vector(0,0,0);
		Vector fromPrev=prev.to(curr).normalize();
		Vector toNext=curr.to(next).normalize();
		Vector prevN=fromPrev.rotate(Math.PI/2, getNormalToCurvePlane());
		Vector nextN=toNext.rotate(Math.PI/2, getNormalToCurvePlane());
		toRet=prevN.add(nextN).mul(0.5).normalize();
		
		return toRet.mul(curr.r);
	}
	
	public Vector getNormalToCurvePlane(){
		Vector toRet=new Vector(0,0,0);
		if(pts.size()>2){
			Vector p1=pts.get(0).to(pts.get(1));
			Vector p2=pts.get(1).to(pts.get(2));
			toRet=p1.crossProd(p2).normalize();	
		}
		return toRet;
	}
	
	public Vector getNormalAtPoint(GeneratedPoint curr, GeneratedPoint next){
		Vector toRet=new Vector(0,0,0);
		Vector toNext=curr.to(next).normalize();
		toRet=toNext.normalize().rotate(Math.PI/2, getNormalToCurvePlane());
		return toRet;	
	}

	
	public Vector getRadialOffsetAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){    	
		Vector norm=getNormalAtPoint(prev,curr, next).normalize();
		Vector tan=norm.rotate(Math.PI/2, getNormalToCurvePlane());
		double dprime1=(curr.r-prev.r)/(prev.to(curr).getMag());
		double dprime2=(next.r-curr.r)/(curr.to(next).getMag());
		double dprime=(dprime1+dprime2)/2;
		Vector nd1=(tan.mul(-dprime));
		Vector nd2=norm.mul(Math.sqrt(1-(dprime*dprime)));
		Vector nd=nd1.add(nd2).mul(Math.abs(curr.r/2));
		return nd;	
	}
	

	
	public Vector getBallOffsetAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){
		Vector normOffset=getNormalOffsetAtPoint(prev, curr, next).mul(0.5);
		Vector radOffset=getRadialOffsetAtPoint(prev, curr, next).mul(0.5);
		return normOffset.add(radOffset);
	}
	
	public void drawOffsetCurve(PApplet p, String mode){
		if (pts.size() > 2) {
			offsetLoop=new PolyLoop();
			genPts = calculateQuinticSpline();
			p.beginShape();
			GeneratedPoint first=genPts.get(0);
			GeneratedPoint second=genPts.get(1);
			GeneratedPoint last=genPts.get(genPts.size()-1);
			GeneratedPoint secondLast=genPts.get(genPts.size()-2);
			int ptCtr=0;
			 
				//Offset vertices on one side
				for (ptCtr = 1; ptCtr < genPts.size() - 1; ptCtr++) {
					GeneratedPoint prev = genPts.get(ptCtr-1);
					GeneratedPoint cur = genPts.get(ptCtr);
					GeneratedPoint next = genPts.get(ptCtr+1);
					Vector offset = new Vector(0,0,0);
					if(mode == "NORMAL")
						offset=getNormalOffsetAtPoint(prev,cur,next);
					else if (mode == "RADIAL")
						offset=getRadialOffsetAtPoint(prev,cur,next);
					else if (mode == "BALL")
						offset=getBallOffsetAtPoint(prev,cur,next);
					Vector offsetPoint=cur.asVec().add(offset);
					p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
					offsetLoop.addPoint(new Point((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z));
				}
			

			//Draw semi-circle at end
			double deltaRot=Math.PI/20;
			Vector offsetAtEnd=getNormalAtPoint(last,secondLast).mul(-last.r/2);
			for(int i=0; i < Math.PI/deltaRot + 1; i++){
				Vector offsetPoint=last.asVec().add(offsetAtEnd);
				p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
				offsetLoop.addPoint(new Point((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z));
				offsetAtEnd=offsetAtEnd.rotate(deltaRot, getNormalToCurvePlane().mul(-1));
			}
			
				
				//Offset vertices on the other side
				ptCtr--;
				while(ptCtr>1){
					GeneratedPoint prev = genPts.get(ptCtr+1);
					GeneratedPoint cur = genPts.get(ptCtr);
					GeneratedPoint next = genPts.get(ptCtr-1);
					Vector offset = new Vector(0,0,0);
					if(offsetMode.equals("NORMAL"))
						offset=getNormalOffsetAtPoint(prev,cur,next);
					else if (offsetMode.equals("RADIAL"))
						offset=getRadialOffsetAtPoint(prev,cur,next);
					else if (offsetMode.equals("BALL"))
						offset=getBallOffsetAtPoint(prev,cur,next);
					Vector offsetPoint=cur.asVec().add(offset);
					p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
					offsetLoop.addPoint(new Point((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z));
					ptCtr--;
				}
			

			//Draw semi-circle at start
			Vector offsetAtStart=getNormalAtPoint(first,second).mul(-first.r/2);
			for(int i=0; i < Math.PI/deltaRot +1; i++){
				Vector offsetPoint=first.asVec().add(offsetAtStart);
				p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
				offsetLoop.addPoint(new Point((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z));
				offsetAtStart=offsetAtStart.rotate(deltaRot, getNormalToCurvePlane().mul(-1));
			}

			p.endShape(PConstants.CLOSE);
		}
	}
	
	public PolyLoop getBoundingLoop(){
		return offsetLoop;
	}
	
	public void drawSpine(PApplet p){
		if(pts.size()>2)
		{
			p.noFill();
			p.stroke(0);
			for(GeneratedPoint g:genPts){
				p.ellipse((float) g.x, (float) g.y, (float) g.r, (float) g.r);
			}
			p.stroke(122, 255, 122);
			for(int ptCtr=0;ptCtr<genPts.size()-1;ptCtr++){
				Point p1=genPts.get(ptCtr);
				Point p2=genPts.get(ptCtr+1);
				p.line((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
			}
		}
	}

	@Override
	public void draw(PApplet p) {
		
		
		if(offsetMode.equals("NORMAL"))	{	p.noFill(); p.stroke(255, 0 ,0); p.strokeWeight(3);}
		if(offsetMode.equals("RADIAL"))	{	p.noFill(); p.stroke(255, 20 , 147); p.strokeWeight(3);}
		if(offsetMode.equals("BALL")) 	{	p.noFill(); p.stroke(0, 0 ,255); p.strokeWeight(3);}
		if(fillCurve)	{
			if(offsetMode.equals("NORMAL"))	{	p.fill(255, 0, 0); }
			if(offsetMode.equals("RADIAL"))	{	p.fill(255, 20, 147); }
			if(offsetMode.equals("BALL") ) 	{	p.fill(0, 0 ,255); }
		}
		drawOffsetCurve(p, offsetMode);
		p.strokeWeight(1);
		if(!fillCurve)	drawSpine(p);

		for (ControlPoint pt : pts) pt.draw(p);

	}
}
