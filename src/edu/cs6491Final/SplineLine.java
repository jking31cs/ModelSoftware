package edu.cs6491Final;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplineLine implements Drawable{

	public CustomLine toCustomLine() {
		return new CustomLine(){{
			this.addAll(calculateQuinticSpline());
		}};
	}

	public static class ControlPoint extends Point {

		public double r;

		public ControlPoint(double r, Point p) {
			super(p.x, p.y, p.z);
			this.r = r;
		}

		@Override
		public void draw(PApplet p) {
			p.stroke(0,255,0);
			p.fill(0, 255, 0);
			p.ellipse((float) x, (float) y, (float) r, (float) r);
		}

	}

	public List<ControlPoint> pts;

	public String offsetMode="RADIAL";

	public int subDivisions=3;

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
			newControlPoints.add(new GeneratedPoint(cp.r, cp));
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
		Vector tan=norm.rotate(Math.PI/2, new Vector(0,0,1));
		double dprime=(curr.r-prev.r)/(prev.to(curr).getMag());
		Vector nd1=tan.mul(-dprime);
		Vector nd=nd1.add(norm).mul(1/Math.sqrt(1+dprime*dprime));

		return nd.mul(curr.r/2);
	}

	public Vector getNormalAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){
		Vector toRet=new Vector(0,0,0);
		Vector fromPrev=prev.to(curr).normalize();
		Vector toNext=curr.to(next).normalize();
		Vector prevN=fromPrev.rotate(Math.PI/2, new Vector(0,0,1));
		Vector nextN=toNext.rotate(Math.PI/2, new Vector(0,0,1));
		toRet=prevN.add(nextN).mul(0.5).normalize();

		return toRet.mul(curr.r);
	}

	public Vector getNormalAtPoint(GeneratedPoint p) {
		GeneratedPoint curr = null;
		GeneratedPoint next = null;
		List<GeneratedPoint> points = calculateQuinticSpline();
		for (int i = 1; i < points.size(); i++) {
			GeneratedPoint g1 = points.get(i-1);
			GeneratedPoint g2 = points.get(i);

			Vector g1_to_p = g1.to(p).normalize();
			Vector g1_to_g2 = g1.to(g2).normalize();
			if (g1.equals(p)
				|| g2.equals(p)
				|| g1_to_p.equals(g1_to_g2)) {
				curr = g1;
				next = g2;
				break;
			}
		}
		if (curr == null) {
			System.out.println(p);
			System.out.println(points);
		}
		return getNormalAtPoint(curr, next);

	}

	public Vector getNormalAtPoint(GeneratedPoint curr, GeneratedPoint next){
		return getTangentAt(curr, next).rotate(Math.PI/2, new Vector(0, 0, 1));
	}

	public Vector getTangentAt(GeneratedPoint curr, GeneratedPoint next) {
		return curr.to(next).normalize();
	}

	
	public Vector getRadialOffsetAtPoint(GeneratedPoint prev,GeneratedPoint curr, GeneratedPoint next){    	
		Vector norm=getNormalAtPoint(prev,curr, next).normalize();
		Vector tan=norm.rotate(Math.PI/2, new Vector(0,0,1));
		double dprime=(curr.r-prev.r)/(prev.to(curr).getMag());
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
		if (pts.size() > 1) {
			List<GeneratedPoint> generatedPoints = calculateQuinticSpline();
			p.beginShape();
			GeneratedPoint first=generatedPoints.get(0);
			GeneratedPoint second=generatedPoints.get(1);
			GeneratedPoint last=generatedPoints.get(generatedPoints.size()-1);
			GeneratedPoint secondLast=generatedPoints.get(generatedPoints.size()-2);
			int ptCtr=0;
			if(pts.size()>2){   
				//Offset vertices on one side
				for (ptCtr = 1; ptCtr < generatedPoints.size() - 1; ptCtr++) {
					GeneratedPoint prev = generatedPoints.get(ptCtr-1);
					GeneratedPoint cur = generatedPoints.get(ptCtr);
					GeneratedPoint next = generatedPoints.get(ptCtr+1);
					Vector offset = new Vector(0,0,0);
					if(mode == "NORMAL")
						offset=getNormalOffsetAtPoint(prev,cur,next);
					else if (mode == "RADIAL")
						offset=getRadialOffsetAtPoint(prev,cur,next);
					else if (mode == "BALL")
						offset=getBallOffsetAtPoint(prev,cur,next);
					Vector offsetPoint=cur.asVec().add(offset);
					p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
				}
			}

			//Draw semi-circle at end
			double deltaRot=Math.PI/20;
			Vector offsetAtEnd=getNormalAtPoint(last,secondLast).mul(-last.r/2);
			for(int i=0; i < Math.PI/deltaRot + 1; i++){
				Vector offsetPoint=last.asVec().add(offsetAtEnd);
				p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
				offsetAtEnd=offsetAtEnd.rotate(deltaRot, new Vector(0, 0, -1));
			}
			if(pts.size() >1)	{
				
				//Offset vertices on the other side
				ptCtr--;
				while(ptCtr>1){
					GeneratedPoint prev = generatedPoints.get(ptCtr+1);
					GeneratedPoint cur = generatedPoints.get(ptCtr);
					GeneratedPoint next = generatedPoints.get(ptCtr-1);
					Vector offset = new Vector(0,0,0);
					if(mode == "NORMAL")
						offset=getNormalOffsetAtPoint(prev,cur,next);
					else if (mode == "RADIAL")
						offset=getRadialOffsetAtPoint(prev,cur,next);
					else if (mode == "BALL")
						offset=getBallOffsetAtPoint(prev,cur,next);
					Vector offsetPoint=cur.asVec().add(offset);
					p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
					ptCtr--;
				}
			}

			//Draw semi-circle at start
			Vector offsetAtStart=getNormalAtPoint(first,second).mul(-first.r/2);
			for(int i=0; i < Math.PI/deltaRot +1; i++){
				Vector offsetPoint=first.asVec().add(offsetAtStart);
				p.vertex((float) offsetPoint.x, (float) offsetPoint.y, (float) offsetPoint.z);
				offsetAtStart=offsetAtStart.rotate(deltaRot, new Vector(0, 0, -1));
			}

			p.endShape(PConstants.CLOSE);
		}
	}
	
	public void drawSpine(PApplet p){
		if(pts.size()>1)
		{
			List<GeneratedPoint> generatedPoints = calculateQuinticSpline();
			p.noFill();
			p.stroke(0);
//			for(GeneratedPoint g:generatedPoints){
//				p.ellipse((float) g.x, (float) g.y, (float) g.r, (float) g.r);
//			}
			p.stroke(122, 255, 122);
			for(int ptCtr=0;ptCtr<generatedPoints.size()-1;ptCtr++){
				Point p1=generatedPoints.get(ptCtr);
				Point p2=generatedPoints.get(ptCtr+1);
				p.line((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
			}
		}
	}

	@Override
	public void draw(PApplet p) {
		
		
		if(offsetMode.equals("NORMAL"))	{	p.noFill(); p.stroke(255, 0 ,0); p.strokeWeight(3);}
		if(Objects.equals(offsetMode, "RADIAL"))	{	p.noFill(); p.stroke(255, 20 , 147); p.strokeWeight(3);}
		if(Objects.equals(offsetMode, "BALL")) 	{	p.noFill(); p.stroke(0, 0 ,255); p.strokeWeight(3);}
		drawOffsetCurve(p, offsetMode);
		p.strokeWeight(1);
		drawSpine(p);

		for (ControlPoint pt : pts) pt.draw(p);

	}
}
