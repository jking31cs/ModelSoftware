import java.util.ArrayList;
import java.util.List;

import edu.cs6491Final.*;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode, editMode;
	
	BezierLine origl1, origl2, l1, l2;
	
	Axis axis;
	
	List<BezierLine> morphLoops;
	
	float dz = -490, rx = -(float) Math.PI/2, ry = 0;
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKey() == 'd' && viewMode) {
			drawMode = true;
			viewMode = false; 
		}
		if (e.getKey() == 'v' && drawMode) {
			calculateLoops();
			viewMode = true;
			drawMode = false;
		}
		if (e.getKey() == 'c') {
			double radius = 10000;
			Vector v = new Vector(radius,0,0);
			Point origin = new Point(width/2, height, 0);
			origl1 = new BezierLine();
			for (BezierLine.ControlPoint p : l1.pts) {
				origl1.addPoint(p);
			}
			origl2 = new BezierLine();
			for (BezierLine.ControlPoint p : l2.pts) {
				origl2.addPoint(p);
			}
			axis = new CircularAxis(
				origin,
				origin.add(v)
			);
		}
		if (e.getKey() == 's' && drawMode) {
			l1 = origl1;
			l2 = origl2;
			axis = new StraightAxis(new Point(width/2, height, 0), new Vector(0,-1,0));
		}
	}
	
	private void calculateLoops() {
		morphLoops.clear();
		double t = 0;
		while (t <= 1) {
			BezierLine lerped = Utils.lerp(axis, l1, l2, t);
			morphLoops.add(lerped);
			t+=(1d/25);
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
			l1.addPoint(point, 10);
			l2.addPoint(Utils.mirrored(point, axis), 10);
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
			Point mousePoint = new Point(e.getX(), e.getY(), 0);
			if (e.isControlDown()) {
				BezierLine.ControlPoint p1 = l1.closestPointWithinRange(
					mousePoint,
					50
				);
				BezierLine.ControlPoint p2 = l2.closestPointWithinRange(
					mousePoint,
					50
				);
				if (p1 == null && p2 == null) return;
				if (p2 == null) {
					p1.r = mousePoint.distanceTo(p1.pt);
				} else if (p1 == null) {
					p2.r = mousePoint.distanceTo(p2.pt);
				} else if (mousePoint.distanceTo(p1.pt) < mousePoint.distanceTo(p2.pt)) {
					p1.r = mousePoint.distanceTo(p1.pt);
				} else {
					p2.r = mousePoint.distanceTo(p2.pt);
				}
			} else {
				for (int i = 0; i < l1.pts.size(); i++) {
					Point p1 = l1.pts.get(i).pt;
					Point p2 = l2.pts.get(i).pt;
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
		l1 = new BezierLine();
		l2 = new BezierLine();
		axis = new StraightAxis(new Point(width/2, height, 0),new Vector(0,-1,0));
		morphLoops = new ArrayList<>();
		translate(width/2, height/2);
		
	}
	
	@Override
	public void draw() {
		background(255);
		if (axis instanceof CircularAxis) {
			CircularAxis ca = (CircularAxis) axis;
			double oldRadius = ca.radius;
			if (oldRadius > 1000) {
				System.out.println("Radius: " + oldRadius);
				axis = new CircularAxis(axis.origin, ca.center.add(new Vector(-100,0,0)));
				l1=Utils.morphAboutAxis(axis, origl1);
				l2=Utils.morphAboutAxis(axis, origl2);
				calculateLoops();
			}
		}
		if (drawMode) {
			axis.draw(this);
			stroke(0);
			strokeWeight(1);
			l1.draw(this);
			l1.drawControlPoints(this);
			stroke(255,0,0);
			l2.draw(this);
			l2.drawControlPoints(this);
		} else {
			pushMatrix();
			camera();
			lights();  // turns on view-dependent lighting
			rotateX(rx); rotateY(ry); // rotates the model around the new origin (center of screen)
			rotateX(PI / 2); // rotates frame around X to make X and Y basis vectors parallel to the floor
			stroke(0,255,0);
			fill(0,255,0);
			strokeWeight(1);
			axis.draw(this);
			stroke(0,0,255);
			for (BezierLine b : morphLoops) b.draw(this);
			for (int i = 0; i < morphLoops.size() - 1; i++) {
				BezierLine m1 = morphLoops.get(i);
				BezierLine m2 = morphLoops.get((i+1) % morphLoops.size());

				List<BezierLine.GeneratedPoint> m1Points = m1.calculateCurve();
				List<BezierLine.GeneratedPoint> m2Points = m2.calculateCurve();
				for (int j = 0; j < m1Points.size() - 1; j++) {
					BezierLine.GeneratedPoint p1 = m1Points.get(j);
					BezierLine.GeneratedPoint p2 = m2Points.get(j);
					BezierLine.GeneratedPoint p3 = m2Points.get((j + 1) % m2Points.size());
					BezierLine.GeneratedPoint p4 = m1Points.get((j + 1) % m1Points.size());
					pushMatrix();
					stroke(0,0,255);
					fill(0,0,255);
					beginShape();
					vertex((float) p1.pt.x, (float) p1.pt.y, (float) p1.pt.z);
					vertex((float) p2.pt.x, (float) p2.pt.y, (float) p2.pt.z);
					vertex((float) p3.pt.x, (float) p3.pt.y, (float) p3.pt.z);
					vertex((float) p4.pt.x, (float) p4.pt.y, (float) p4.pt.z);
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
