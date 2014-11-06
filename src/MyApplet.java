import java.util.ArrayList;
import java.util.List;

import edu.cs6491Final.Point;
import edu.cs6491Final.PolyLoop;
import edu.cs6491Final.Utils;
import edu.cs6491Final.Vector;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode, editMode;
	
	PolyLoop l1, l2;
	
	Point  axisPoint;
	Vector axis;
	
	List<PolyLoop> morphLoops;
	
	float dz = -490, rx = -.1832594f, ry = -.6479535f;
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKey() == 'd') {
			drawMode = true;
			viewMode = false; 
		}
		if (e.getKey() == 'v') {
			calculateLoops();
			viewMode = true;
			drawMode = false;
		}
	}
	
	private void calculateLoops() {
		morphLoops.clear();
		double t = 0;
		while (t <= 1) {
			PolyLoop lerped = Utils.lerp(axisPoint, axis, l1, l2, t);
			morphLoops.add(lerped);
			t+=(1d/6);
		}
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
			l2.addPoint(Utils.mirrored(point, axisPoint, axis));
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
		axisPoint = new Point(width/2, height, 0);
		axis = new Vector(0,-1,0).mul(this.height);
		morphLoops = new ArrayList<>();
		
	}
	
	@Override
	public void draw() {
		background(255);
		if (drawMode) { 
			stroke(0,255,0);
			strokeWeight(5);
			PolyLoop axisLoop = new PolyLoop();
			axisLoop.addPoint(axisPoint);
			axisLoop.addPoint(axisPoint.add(axis));
			axisLoop.draw(this);
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
			rotateX(PI/2); // rotates frame around X to make X and Y basis vectors parallel to the floor
			stroke(0);
			for (PolyLoop l : morphLoops) {
				l.draw(this);
			}
			popMatrix();
		}
	}

	public static void main(String[] args) {
		PApplet.main("MyApplet");
	}

}
