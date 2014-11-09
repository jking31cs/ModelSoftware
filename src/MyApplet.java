import java.util.ArrayList;
import java.util.List;

import edu.cs6491Final.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode, editMode;
	
	PolyLoop l1, l2;
	
	Axis axis;
	
	List<PolyLoop> morphLoops;
	
	float dz = -490, rx = -(float) Math.PI/2, ry = 0;
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKey() == 'd') {
			drawMode = true;
			viewMode = false; 
		}
		if (e.getKey() == 'v') {
			shiftOrigin();
			calculateLoops();
			viewMode = true;
			drawMode = false;
		}
		if (e.getKey() == 'c' && drawMode) {
			double radius = 1000;
			Vector v = new Vector(radius,0,0);
			Point origin = new Point(width/2, height, 0);
			axis = new CircularAxis(
				origin,
				origin.add(v)
			);
			l1=Utils.morphAboutAxis(axis, l1);
			l2=Utils.morphAboutAxis(axis, l2);
		}
	}
	
	private void calculateLoops() {
		morphLoops.clear();
		double t = 0;
		while (t <= 1) {
			PolyLoop lerped = Utils.lerp(axis, l1, l2, t);
			morphLoops.add(lerped);
			t+=(1d/6);
		}
	}
	
	private void shiftOrigin(){
		if(!(axis instanceof CircularAxis)){
			axis.origin.x-=width/2;
			for(Point p:l1.points){
				p.x-=width/2;
				p.y-=height/2;
			}
			for(Point p:l2.points){
				p.x-=width/2;
				p.y-=height/2;
			}
		}/*else{
			axis.origin.x-=width/2;
			((CircularAxis)axis).center.x-=width/2;
			for(Point p:l1.points){
				p.x-=width/2;
				p.y-=height/2;
			}
			for(Point p:l2.points){
				p.x-=width/2;
				p.y-=height/2;
			}
		}*/
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (editMode) {
			editMode = false;
			return;
		}
		if (drawMode) {
			Point point = new Point(e.getX(), e.getY(), 0);
			l1.addPoint(point);
			l2.addPoint(Utils.mirrored(point, axis));
		}
	}
	
	@Override
	public void mouseMoved() {
		if (keyPressed && key==' ') {
			rx-=PI*(mouseY-pmouseY)/height;
			ry+=PI*(mouseX-pmouseX)/width;
		}
	}	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (drawMode) {
			Point mousePoint = new Point (e.getX(), e.getY(), 0);
			for (int i = 0; i < l1.points.size(); i++) {
				Point p1 = l1.points.get(i);
				Point p2 = l2.points.get(i);
				if (p1.distanceTo(mousePoint) < 15) {
					p1.x = mousePoint.x;
					p1.y = mousePoint.y;
					break;
				}
				if (p2.distanceTo(mousePoint) < 15) {
					p2.x = mousePoint.x;
					p2.y = mousePoint.y;
					break;
				}
			}
			editMode = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseWheel(MouseEvent event) {
		dz -= event.getAmount(); 
	}
	
	@Override
	public void setup() {
		size(1024,768,P3D);
		drawMode = true;
		l1 = new PolyLoop();
		l2 = new PolyLoop();
		axis = new StraightAxis(new Point(width/2, height, 0),new Vector(0,-1,0));
		morphLoops = new ArrayList<>();
		
	}
	
	@Override
	public void draw() {
		background(255);
		if (drawMode) {
			axis.draw(this);
			stroke(0);
			strokeWeight(1);
			l1.draw(this);
			stroke(255,0,0);
			l2.draw(this);
		} else {
			pushMatrix();
			camera();
			translate(width/2,height/2,dz); // puts origin of model at screen center and moves forward/away by dz
			lights();  // turns on view-dependent lighting
			rotateX(rx); rotateY(ry); // rotates the model around the new origin (center of screen)
			rotateX(PI / 2); // rotates frame around X to make X and Y basis vectors parallel to the floor
			axis.draw(this);
			stroke(0);
			for (int i = 0; i < morphLoops.size() - 1; i++) {
				PolyLoop m1 = morphLoops.get(i);
				PolyLoop m2 = morphLoops.get((i+1) % morphLoops.size());
				for (int j = 0; j < m1.points.size(); j++) {
					Point p1 = m1.points.get(j);
					Point p2 = m2.points.get(j);
					Point p3 = m2.points.get((j+1) % m1.points.size());
					Point p4 = m1.points.get((j+1) % m1.points.size());
					pushMatrix();
					fill(0,0,255);
					beginShape();
					vertex((float) p1.x, (float) p1.y, (float) p1.z);
					vertex((float) p2.x, (float) p2.y, (float) p2.z);
					vertex((float) p3.x, (float) p3.y, (float) p3.z);
					vertex((float) p4.x, (float) p4.y, (float) p4.z);
					endShape(CLOSE);
					popMatrix();
				}

			}
			popMatrix();
		}
	}

	public static void main(String[] args) {
		PApplet.main("MyApplet");
	}

}
