package edu.cs6491Final;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by bobby on 11/22/14.
 */
public class CustomLine extends ArrayList<GeneratedPoint> implements Drawable {

	public String offsetMode = "NORMAL";
	public PolyLoop offsetLoop = new PolyLoop();

	@Override
	public void draw(PApplet p) {

		for (GeneratedPoint pt : this) {
			p.ellipseMode(PConstants.CENTER);
			p.ellipse((float) pt.x, (float) pt.y, (float) pt.r, (float) pt.r);
		}
		for (int i = 1; i < this.size(); i++) {
			Point p1 = this.get(i-1);
			Point p2 = this.get(i);
			p.line((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		}
		p.noFill();

		if(offsetMode.equals("NORMAL"))	{	p.noFill(); p.stroke(255, 0 ,0); p.strokeWeight(3);}
		if(offsetMode.equals("RADIAL"))	{	p.noFill(); p.stroke(255, 20 , 147); p.strokeWeight(3);}
		if(offsetMode.equals("BALL")) 	{	p.noFill(); p.stroke(0, 0 ,255); p.strokeWeight(3);}
		drawOffsetCurve(p, offsetMode);
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

	public Vector getNormalToCurvePlane() {
		return new Vector(0,0,1);
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
		try {
			if (this.size() > 3) {
				offsetLoop = new PolyLoop();
				p.beginShape();
				GeneratedPoint first=this.get(0);
				GeneratedPoint second=this.get(1);
				GeneratedPoint last=this.get(this.size() - 1);
				GeneratedPoint secondLast=this.get(this.size() - 2);
				int ptCtr=0;

				//Offset vertices on one side
				for (ptCtr = 1; ptCtr < this.size() - 1; ptCtr++) {
					GeneratedPoint prev = this.get(ptCtr - 1);
					GeneratedPoint cur = this.get(ptCtr);
					GeneratedPoint next = this.get(ptCtr + 1);
					Vector offset = new Vector(0,0,0);
					if(Objects.equals(mode, "NORMAL"))
						offset=getNormalOffsetAtPoint(prev,cur,next);
					else if (Objects.equals(mode, "RADIAL"))
						offset=getRadialOffsetAtPoint(prev,cur,next);
					else if (Objects.equals(mode, "BALL"))
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
					GeneratedPoint prev = this.get(ptCtr + 1);
					GeneratedPoint cur = this.get(ptCtr);
					GeneratedPoint next = this.get(ptCtr - 1);
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
		} catch (ArrayIndexOutOfBoundsException e) {
			//ignore
		}

	}
}
