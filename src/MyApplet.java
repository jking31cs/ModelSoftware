import java.util.ArrayList;
import java.util.List;

import edu.cs6491Final.*;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode, editMode;
	boolean revolve2DMode = false;
	boolean animating = true;
	boolean controlPointMoved = false;
	private List<Vector> norms;
	int debug = 0;
	
	PolyLoop origl1, origl2, l1, l2;
	private double increment = 0;
	private double revolveMax = 360d;
	Axis axis;
	private Point F = new Point(0,0,0);
	
	List<PolyLoop> morphLoops;
	
	float dz = -490, rx = -(float) Math.PI/2, ry = 0;
	
	@Override
	public void setup() {
		size(1024,768,P3D);
		drawMode = true;
		l1 = new PolyLoop();
		l2 = new PolyLoop();
		axis = new StraightAxis(new Point(width/2, height, 0),new Vector(0,-1,0));
		morphLoops = new ArrayList<>();
		translate(width/2, height/2);
		Utils.appHeight = height;
		//Utils.applet = this;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKey() =='f') { // move focus point on plane
		    F.sub(F.ToIJ(new Vector((float)(mouseX-pmouseX),(float)(mouseY-pmouseY),0))); 
	    }
	    if(e.getKey() == 'l'){
	    	debug++;
	    }
	    if(e.getKey() == 'k'){
	    	debug--;
	    }
		if (e.getKey() == 'd' && viewMode) {
			drawMode = true;
			viewMode = false; 
		}
		if (e.getKey() == 'v' && drawMode) {
			calculateLoops();
			viewMode = true;
			drawMode = false;
		}
		if (e.getKey() == '3'){
			revolve2DMode = !revolve2DMode;
		}
		if(e.getKey() == '['){
			increment -= 10;
			if(increment < 0) increment = 0d;
			System.out.println(increment);
			animating = false;
		}
		if(e.getKey() == ']'){
			increment += 10;
			if(increment >= revolveMax) increment = revolveMax;
			System.out.println(increment);
			animating = false;
		}
		if(e.getKey() == 'p'){
			animating = !animating;
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
		if (e.getKey() == 'q') {
			origl1 = new PolyLoop();
			System.out.println("initial l1 points : ");
			for (Point p : l1.points) {
				origl1.addPoint(p);
				System.out.println(p.toString());
			}
			origl2 = new PolyLoop();
			for (Point p : l2.points) {
				origl2.addPoint(p);
			}

			List<Point> points = new ArrayList<>();
			int numControlPoints = 6;
			for (int i = 0; i < numControlPoints; i++) {
				Point p = new Point(width/2, 0.1*height + 0.8*height/(numControlPoints-1) * i, 0);
				points.add(p);
			}

			//l1.SetBValues(height);
			//l2.SetBValues(height);
			axis = new SplineAxis(new Point(width/2, height, 0), points, this, l1, l2);
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
		while (t <= increment/180d) {
			System.out.println(t);
			PolyLoop lerped = Utils.lerp(axis, l1, l2, t);
			morphLoops.add(lerped);
			t += (1d/10);
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

			if(axis instanceof SplineAxis) {
				for( Point pt : ((SplineAxis)axis).controlPoints ){
					if(pt.distanceTo(mousePoint) < 30) {
						pt.x = mousePoint.x;
						pt.y = mousePoint.y;
						((SplineAxis)axis).quintic();
						controlPointMoved = true;
						break;
					}
				} 
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseWheel(MouseEvent event) {
		dz -= event.getAmount(); 
	}
	
	@Override
	public void draw() {
		background(255);
		translate((float)-F.x,(float)-F.y,(float)-F.z);
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
		} else if (axis instanceof SplineAxis) {
			if(controlPointMoved) {
				l1 = Utils.morphAboutAxis(axis, l1);
				l2 = Utils.morphAboutAxis(axis, l2);
				controlPointMoved = false;
				calculateLoops();
			}
			((SplineAxis)axis).UpdateLoopRefs(l1, l2);
		}
		if (drawMode) {
			stroke(0,0,255);
			axis.draw(this);

			strokeWeight(1);
			stroke(0);
			l1.draw(this);

			stroke(255,0,0);
			l2.draw(this);
		} else {
			calculateLoops();
			if(viewMode && animating) {
				if(revolveMax >= increment)
				increment += 2d;
			}
			pushMatrix();
			camera();
			lights();  // turns on view-dependent lighting
			//pointLight(255, 255, 255, width/2, height/2, 0);
			rotateX(rx); rotateY(ry); // rotates the model around the new origin (center of screen)
			rotateX(PI / 2); // rotates frame around X to make X and Y basis vectors parallel to the floor
			axis.draw(this);
			stroke(0);
			if(revolve2DMode){
				PolyLoop last = morphLoops.get(morphLoops.size()-1);
				last.draw(this);
			} else {
				//calculating normals for lighting and smoothing
				norms = new ArrayList<Vector>();
				for (int i = 0; i < morphLoops.size() - 1; i++) {
					PolyLoop m1 = morphLoops.get(i);
					PolyLoop m2 = morphLoops.get((i+1) % morphLoops.size());
					for (int j = 0; j < m1.points.size(); j++) {
						Point p1 = m1.points.get(j);
						Point p2 = m2.points.get(j);
						Point p3 = m2.points.get((j+1) % m1.points.size());

						Vector v1 = new Vector(p1, p2);
						Vector v2 = new Vector(p1, p3);
						//v1 = v1.normalize();
						//v2 = v2.normalize();
						Vector norm = v1.crossProd(v2);

						/*if (i == 0 && j == debug) {
							p1.draw(this);
							stroke(0,0,255);
							p2.draw(this);
							stroke(255, 0, 0);
							p3.draw(this);

							stroke(0,255,0);
							v1.draw(this, p1);
							v2.draw(this, p1);
							stroke(255, 0, 0);
							norm.draw(this, p1);						
						}*/

						norm = norm.normalize();

						norms.add(norm);
					}
				}

				//drawing geo
				for (int i = 0; i < morphLoops.size() - 1; i++) {
					PolyLoop m1 = morphLoops.get(i);
					PolyLoop m2 = morphLoops.get((i+1) % morphLoops.size());
					for (int j = 0; j < m1.points.size(); j++) {
						Point p1 = m1.points.get(j);
						Point p2 = m2.points.get(j);
						Point p3 = m2.points.get((j+1) % m1.points.size());
						Point p4 = m1.points.get((j+1) % m1.points.size());
						pushMatrix();
						stroke(0,0,255);
						fill(0,0,255);
						beginShape();
						FindNormal(i, j);
						vertex((float) p1.x, (float) p1.y, (float) p1.z);
						vertex((float) p2.x, (float) p2.y, (float) p2.z);
						vertex((float) p3.x, (float) p3.y, (float) p3.z);
						vertex((float) p4.x, (float) p4.y, (float) p4.z);
						endShape(CLOSE);
						popMatrix();
					}
				}
				//draw caps!
				if(increment != 360){
					PolyLoop loop = morphLoops.get(0);
					pushMatrix();
					beginShape();
					for(Point p : loop.points){
						vertex((float)p.x, (float)p.y, (float)p.z);
					}
					endShape(CLOSE);
					popMatrix();
				}
				if(increment != 360){
					PolyLoop loop = morphLoops.get(morphLoops.size()-1);
					pushMatrix();
					beginShape();
					for(Point p : loop.points){
						vertex((float)p.x, (float)p.y, (float)p.z);
					}
					endShape(CLOSE);
					popMatrix();
				}
			}
			popMatrix();
		}
	}

	public void FindNormal(int i1, int i2){
		PolyLoop l = morphLoops.get(i1);

		List<Vector> ns = new ArrayList<Vector>();
		Vector n1 = norms.get(i1 * l.points.size() + i2);

		//n1.draw(this, l.points.get(i2));
		ns.add(n1);

		Vector avgNorms = n1;
		for(int i = 1; i < ns.size()-1; i++){
			Vector addV = ns.get(i);
			avgNorms = avgNorms.add(addV);
		}
		avgNorms = avgNorms.normalize();

		normal((float)avgNorms.x, (float)avgNorms.y, (float)avgNorms.z);
		//normal((float)n1.x, (float)n1.y, (float)n1.z);
	}

	public static void main(String[] args) {
		PApplet.main("MyApplet");
	}

}
