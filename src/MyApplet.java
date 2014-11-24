import edu.cs6491Final.Axis;
import edu.cs6491Final.CircularAxis;
import edu.cs6491Final.Point;
import edu.cs6491Final.PolyLoop;
import edu.cs6491Final.SplineAxis;
import edu.cs6491Final.SplineLine;
import edu.cs6491Final.StraightAxis;
import edu.cs6491Final.Utils;
import edu.cs6491Final.Vector;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGL;
import processing.opengl.PGraphics3D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode, editMode;
	boolean revolve2DMode = false;
	boolean animating = true;
	boolean controlPointMoved = false;
	boolean fDragging = false;
	boolean outputVolume = false;
	private List<Vector> norms;
	int debug = 0;
	
	PolyLoop origl1, origl2, l1, l2;
	
	SplineLine sl1,sl2;
	
	private double increment = 0;
	private double revolveMax = 360d;
	Axis axis;
	private Point F = new Point(0,0,0);
	PImage hatch;
	//PGraphicsOpenGL popengl;
	
	List<PolyLoop> morphLoops;
	
	float dz = 0, rx = -(float) Math.PI/2, ry = 0;

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
		if (e.getKey() == 'e' && drawMode) {
			calculateLoops();
			viewMode = true;
			drawMode = false;
		}
		if (e.getKey() == 'v' && viewMode) {
			outputVolume = true;
		}
		if (e.getKey() == '3'){
			revolve2DMode = !revolve2DMode;
		}
		if(e.getKey() == '['){
			increment -= 10;
			if(increment < 0) increment = 0d;
			animating = false;
		}
		if(e.getKey() == ']'){
			increment += 10;
			if(increment >= revolveMax) increment = revolveMax;
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

			axis = new SplineAxis(new Point(width/2, height, 0), points, this, l1, l2);
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
//    	if(e.getKey()==']')	{sl1.subDivisions++; sl2.subDivisions++;}
//    	if(e.getKey()=='[')	{sl1.subDivisions--; sl2.subDivisions--;}
    	if(e.getKey()=='f')	{sl1.fillCurve=!sl1.fillCurve; sl2.fillCurve=!sl2.fillCurve;}
	}
	
	private void calculateLoops() {
		morphLoops.clear();
		double t = 0;
		while (t <= increment/180d) {
			//System.out.println(t);
			PolyLoop morphed = Utils.ballMorphLerp(axis, sl1, sl2, t);
			morphLoops.add(morphed);
			t += (1d/10);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		fDragging = false;
		if (editMode) {
			editMode = false;
			return;
		}
		if (drawMode) {
			//Point point = new Point(e.getX(), e.getY(), 0);
			//l1.addPoint(point);
			//l2.addPoint(Utils.mirrored(point, axis));
		}
		l1 = sl1.getBoundingLoop();
		l2 = sl2.getBoundingLoop();
	}
	
	public void mouseClicked(MouseEvent e) {
        if (sl1.closestPointWithinRange(new Point(e.getX(), e.getY(), 0), 20) == null)
        {
        	Point toAdd=new Point(e.getX(), e.getY(), 0);
            sl1.addPoint(toAdd, 10);
            sl2.addPoint(Utils.mirrored(toAdd, axis), 10);
			l1 = sl1.getBoundingLoop();
			l2 = sl2.getBoundingLoop();
			if (l1 != null) {
				System.out.println("I'm making a loop man: " + l1.points);
			}
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
	                pt1.r = mousePoint.distanceTo(pt1);
	            } else {
	                pt1.x = mousePoint.x;
	                pt1.y = mousePoint.y;
	            }
	        }
	        if (pt2 != null) {
	            if (e.isControlDown()) {
	                pt2.r = mousePoint.distanceTo(pt2);
	            } else {
	                pt2.x = mousePoint.x;
	                pt2.y = mousePoint.y;
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
						fDragging = true;
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
	public void setup() {
		size(1024, 768, P3D);
		Utils.appHeight = height;
		hatch = loadImage("./hatchPattern.jpg");
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
			//if(controlPointMoved) {
				rotateX(rx); rotateY(ry); // rotates the model around the new origin (center of screen)
				rotateX(PI / 2); // rotates frame around X to make X and Y basis vectors parallel to the floor
			
				l1 = Utils.morphAboutAxis(axis, origl1);
				l2 = Utils.morphAboutAxis(axis, origl2);

				controlPointMoved = false;
			//}
			calculateLoops();
			((SplineAxis)axis).UpdateLoopRefs(l1, l2);

			//figure out which control point we're manipulating
			if ( fDragging != true ){
				Utils.g_center = pick( mouseX, mouseY );
				((SplineAxis)axis).idOfVertexWithClosestScreenProjectionTo(new Point(mouseX, mouseY, 0));
			}
			fill(color(255, 0, 0),100); 
			Point showPt = ((SplineAxis)axis).showPicked();
			showPt.draw(this);

		    /*if ( Utils.g_center != null )
		    {
		      pushMatrix();
		      translate((float)Utils.g_center.x,(float)Utils.g_center.y,(float)Utils.g_center.z );
		      sphere(2);
		      translate( (float)-Utils.g_center.x, (float)-Utils.g_center.y, (float)-Utils.g_center.z );
		      popMatrix();
		    }*/
		    if (keyPressed && key=='z') {
				((SplineAxis)axis).movePicked(new Vector(0, 0, mouseY - pmouseY));
			}
		}
		if (drawMode) {
			stroke(0, 0, 255);
			axis.draw(this);

			strokeWeight(1);
			stroke(0);
			sl1.draw(this);
			if (l1 != null) l1.draw(this);
			
			stroke(255,0,0);
			sl2.draw(this);
			if (l2 != null) l2.draw(this);

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
			translate(0,0,dz);
			smooth();
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
						norm = norm.normalize();

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
						//stroke(0,0,255);
						noStroke();
						fill(0,0,255);
						beginShape();
						Vector norm = FindNormal(i, j);
						normal((float) norm.x, (float) norm.y, (float) norm.z);
						vertex((float) p1.x, (float) p1.y, (float) p1.z);
						vertex((float) p2.x, (float) p2.y, (float) p2.z);
						vertex((float) p3.x, (float) p3.y, (float) p3.z);
						vertex((float) p4.x, (float) p4.y, (float) p4.z);
						endShape(CLOSE);
						popMatrix();
					}
				}

				textureWrap(REPEAT);
				//draw caps!
				if(increment != 360){

					// START CAP
					PolyLoop firstLoop = morphLoops.get(0);
					Point A = firstLoop.points.get(0);
					Point B = firstLoop.points.get(1);
					Point C = firstLoop.points.get(2);

					Vector tan = (A.to(B)).normalize();
					Vector norm = ((B.to(A)).normalize()).crossProd((C.to(B)).normalize());
					Vector binorm = norm.crossProd(tan);

					/*stroke(0,0,0);
					A.draw(this);
					stroke(0,0,255);
					B.draw(this);
					stroke(255, 0, 0);
					C.draw(this);

					stroke(0,255,0);
					tan.draw(this, A);
					norm.draw(this, A);
					binorm.draw(this, A);*/

					pushMatrix();
					beginShape();
					texture(hatch);
					noFill();
					for(Point p : firstLoop.points){
						Vector AP = A.to(p);
						float u = (float)(AP.dotProduct(tan));
						float v = (float)(AP.dotProduct(binorm));
						vertex((float)p.x, (float)p.y, (float)p.z, u, v);
					}
					endShape(CLOSE);
					popMatrix();



					// END CAP
					PolyLoop lastLoop = morphLoops.get(morphLoops.size()-1);
					A = lastLoop.points.get(0);
					B = lastLoop.points.get(1);
					C = lastLoop.points.get(2);

					tan = (A.to(B)).normalize();
					norm = ((B.to(A)).normalize()).crossProd((C.to(B)).normalize());
					binorm = norm.crossProd(tan);

					pushMatrix();
					beginShape();
					texture(hatch);
					noFill();
					for(Point p : lastLoop.points){
						Vector AP = A.to(p);
						float u = (float)(AP.dotProduct(tan));
						float v = (float)(AP.dotProduct(binorm));
						vertex((float)p.x, (float)p.y, (float)p.z, u, v);
					}
					endShape(CLOSE);
					popMatrix();
				}

				if (outputVolume) {
					System.out.println("//// TOTAL VOLUME = " + FindTotalVolume());
					outputVolume = false;
				}
			}
			popMatrix();
		}
	}

	public double FindTotalVolume() {
		double total = 0d;
		for (int i = 0; i < morphLoops.size()-1; i++) {
			total += FindInterloopVolume(morphLoops.get(i), morphLoops.get((i+1) % morphLoops.size()));
		}

		return total;
	}

	public double FindInterloopVolume(PolyLoop loop1, PolyLoop loop2) {
		Point com1 = loop1.COM();
		Point com2 = loop2.COM();
		double area1 = loop1.area();
		double area2 = loop2.area();

		return (area1 + area2)/2d * com1.distanceTo(com2);
	}

	public Vector FindNormal(int i1, int i2){
		PolyLoop l = morphLoops.get(i1);

		List<Vector> ns = new ArrayList<Vector>();
		Vector n1 = norms.get(i1 * l.points.size() + i2);
		//n1.draw(this, l.points.get(i2));
		ns.add(n1);


		if(mousePressed) {
			//get faces on l/r of this one
			if(((i1+1) * l.points.size() + i2) < l.points.size()) {
				Vector n2 = norms.get((i1+1) * l.points.size() + i2); 
				ns.add(n2);
			}
			if(((i1-1) * l.points.size() + i2) > 0) {
				Vector n2 = norms.get((i1-1) * l.points.size() + i2); 
				ns.add(n2);
			}

			//get face on top/bottom of this one
			if(i2+1 >= l.points.size()) { //loop around to point 0
				Vector n2 = norms.get(i1 * l.points.size()); 
				ns.add(n2);
			} else {
				Vector n2 = norms.get(i1 * l.points.size() + i2+1); 
				ns.add(n2);
			}

			if(i2-1 < 0) { //loop around to point 0
				Vector n2 = norms.get(i1 * l.points.size() + l.points.size()-1); 
				ns.add(n2);
			} else {
				Vector n2 = norms.get(i1 * l.points.size() + i2-1); 
				ns.add(n2);
			}
		}


		Vector avgNorms = n1;
		for(int i = 1; i < ns.size()-1; i++){
			Vector addV = ns.get(i);
			avgNorms = avgNorms.add(addV);
		}
		avgNorms = avgNorms.normalize();

		return avgNorms;
		//normal((float)n1.x, (float)n1.y, (float)n1.z);
	}

	public static void main(String[] args) {
		PApplet.main("MyApplet");
	}

	public Point pick(int mX, int mY)
	{
	  PGL pgl = beginPGL();
	  //GL2 gl = g.beginPGL.gl;
	  FloatBuffer depthBuffer = ByteBuffer.allocateDirect(1 << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
	  pgl.readPixels(mX, this.height - mY - 1, 1, 1, PGL.DEPTH_COMPONENT, PGL.FLOAT, depthBuffer);
	  float depthValue = depthBuffer.get(0);
	  depthBuffer.clear();
	  endPGL();

	  //get 3d matrices
	  PGraphics3D p3d = (PGraphics3D)g;
	  PMatrix3D proj = p3d.projection.get();
	  PMatrix3D modelView = p3d.modelview.get();
	  PMatrix3D modelViewProjInv = proj; 
	  modelViewProjInv.apply( modelView ); 
	  modelViewProjInv.invert();
	  
	  float[] viewport = {0, 0, p3d.width, p3d.height};
	  
	  float[] normalized = new float[4];
	  normalized[0] = ((mX - viewport[0]) / viewport[2]) * 2.0f - 1.0f;
	  normalized[1] = ((height - mY - viewport[1]) / viewport[3]) * 2.0f - 1.0f;
	  normalized[2] = depthValue * 2.0f - 1.0f;
	  normalized[3] = 1.0f;
	  
	  float[] unprojected = new float[4];
	  
	  modelViewProjInv.mult( normalized, unprojected );
	  return new Point( unprojected[0]/unprojected[3], unprojected[1]/unprojected[3], unprojected[2]/unprojected[3] );
	}

}
