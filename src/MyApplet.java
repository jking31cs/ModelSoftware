import java.util.ArrayList;
import java.util.List;

import edu.cs6491Final.*;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode, editMode;
	
	PolyLoop origl1, origl2, l1, l2;
	
	SplineLine sl1,sl2;
	
	Axis axis;
	
	List<PolyLoop> morphLoops;
	
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
			origl1 = new PolyLoop();
			for (Point p : l1.points) {
				origl1.addPoint(p);
			}
			origl2 = new PolyLoop();
			for (Point p : l2.points) {
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
		if (e.getKey() == 't' && drawMode) {
			l1.tuck(2d/3);
			l1.tuck(-2d/3);
			l2.tuck(2d/3);
			l2.tuck(-2d/3);
		}
		if(e.getKey()=='n')	{sl1.offsetMode="NORMAL"; sl2.offsetMode="NORMAL";}
    	if(e.getKey()=='r')	{sl1.offsetMode="RADIAL"; sl2.offsetMode="RADIAL";}
    	if(e.getKey()=='b')	{sl1.offsetMode="BALL"; sl2.offsetMode="BALL";}
    	if(e.getKey()==']')	{sl1.subDivisions++; sl2.subDivisions++;}
    	if(e.getKey()=='[')	{sl1.subDivisions--; sl2.subDivisions--;}
    	if(e.getKey()=='f')	{sl1.fillCurve=!sl1.fillCurve; sl2.fillCurve=!sl2.fillCurve;}
	}
	
	private void calculateLoops() {
		morphLoops.clear();
		double t = 0;
		while (t <= 1) {
			PolyLoop lerped = Utils.lerp(axis, l1, l2, t);
			morphLoops.add(lerped);
			t+=(1d/100);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (editMode) {
			editMode = false;
			return;
		}
		if (drawMode) {
			//Point point = new Point(e.getX(), e.getY(), 0);
			//l1.addPoint(point);
			//l2.addPoint(Utils.mirrored(point, axis));
		}
	}
	
	public void mouseClicked(MouseEvent e) {
        if (sl1.closestPointWithinRange(new Point(e.getX(), e.getY(), 0), 20) == null)
        {
        	Point toAdd=new Point(e.getX(), e.getY(), 0);
            sl1.addPoint(toAdd, 10);
            sl2.addPoint(Utils.mirrored(toAdd, axis), 10);
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
	        SplineLine.ControlPoint pt1 = sl1.closestPointWithinRange(
	            mousePoint, 100
	        );
	        SplineLine.ControlPoint pt2 = sl2.closestPointWithinRange(
		            mousePoint, 100
		        );
	        if (pt1 != null) {
	            if (e.isControlDown()) {
	                pt1.r = mousePoint.distanceTo(pt1.pt);
	            } else {
	                pt1.pt.x = mousePoint.x;
	                pt1.pt.y = mousePoint.y;
	            }
	        }
	        if (pt2 != null) {
	            if (e.isControlDown()) {
	                pt2.r = mousePoint.distanceTo(pt2.pt);
	            } else {
	                pt2.pt.x = mousePoint.x;
	                pt2.pt.y = mousePoint.y;
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
		sl1=new SplineLine();
		sl2=new SplineLine();
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
			sl1.draw(this);
			l1=sl1.getBoundingLoop();
			
			stroke(255,0,0);
			sl2.draw(this);
			l2=sl2.getBoundingLoop();
		
		} else {
			pushMatrix();
			camera();
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
