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
	boolean wireframe = false;
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
	
	float eyeX,eyeY,eyeZ;

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKey() == 'w') { // wireframe mode toggle
			wireframe = !wireframe;
		}
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
        	if (drawMode) {
	        	Point toAdd=new Point(e.getX(), e.getY(), 0);
	            sl1.addPoint(toAdd, 10);
	            sl2.addPoint(Utils.mirrored(toAdd, axis), 10);
	            l1=sl1.getBoundingLoop();
	        	l2=sl2.getBoundingLoop();
	        }
        }
    }
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (keyPressed && key == ' ') {
			eyeY -= mouseY-pmouseY;
			eyeX += mouseX-pmouseX;
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
	        l1=sl1.getBoundingLoop();
        	l2=sl2.getBoundingLoop();
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
		eyeZ += event.getAmount() * 25;
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
		eyeX = width/2;
		eyeY = height/2;
		eyeZ = 500;

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
		} else if (axis instanceof SplineAxis) {
			l1 = Utils.morphAboutAxis(axis, origl1);
			l2 = Utils.morphAboutAxis(axis, origl2);

			controlPointMoved = false;
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
//			if (l1 != null) l1.draw(this);
			
			//if(l1 !=null) l1.draw(this);
			stroke(255,0,0);
			sl2.draw(this);
//			if (l2 != null) l2.draw(this);

		} else {
			calculateLoops();
			if(viewMode && animating) {
				if(revolveMax >= increment)
					increment += 2d;
			}

			if (outputVolume) {
				System.out.println("//////// TOTAL VOLUME = " + FindTotalVolume());
				outputVolume = false;
			}

			pushMatrix();
			camera(eyeX, eyeY, eyeZ, width / 2, height / 2, 0, 0, 1, 0);
			lights();  // turns on view-dependent lighting
			//pointLight(255, 255, 255, width/2, height/2, 0);
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
						int i1 = i;
						int j1 = j;
						Point p2 = m2.points.get(j);
						int i2 = (i+1) % morphLoops.size();
						int j2 = j;
						Point p3 = m2.points.get((j+1) % m1.points.size());
						int i3 = (i+1) % morphLoops.size();
						int j3 = (j+1) % m1.points.size();
						Point p4 = m1.points.get((j+1) % m1.points.size());
						int i4 = i;
						int j4 = (j+1) % m1.points.size();

						pushMatrix();

						if (wireframe) {
							noFill();

							Vector norm = FindNormal(i, j);
							normal((float) norm.x, (float) norm.y, (float) norm.z);

							// set stroke color to light blue when the next edge is a silhoette edge
							beginShape(LINES);
							if (IsSilhouette(i1, j1, i2, j2)) {
								stroke(0,0,255, 255);
								strokeWeight(5);
							} else {
								stroke(0,0,255, 25);
								strokeWeight(1);
							}
							vertex((float) p1.x, (float) p1.y, (float) p1.z);
							vertex((float) p2.x, (float) p2.y, (float) p2.z);
							endShape();

							beginShape(LINES);
							if (IsSilhouette(i2, j2, i3, j3)) {
								stroke(0,0,255, 255);
								strokeWeight(5);
							} else {
								stroke(0,0,255, 25);
								strokeWeight(1);
							}
							vertex((float) p2.x, (float) p2.y, (float) p2.z);
							vertex((float) p3.x, (float) p3.y, (float) p3.z);
							endShape();

							beginShape(LINES);
							if (IsSilhouette(i3, j3, i4, j4)) {
								stroke(0,0,255, 255);
								strokeWeight(5);
							} else {
								stroke(0,0,255, 25);
								strokeWeight(1);
							}
							vertex((float) p3.x, (float) p3.y, (float) p3.z);
							vertex((float) p4.x, (float) p4.y, (float) p4.z);
							endShape();

							beginShape(LINES);
							if (IsSilhouette(i4, j4, i1, j1)) {
								stroke(0,0,255, 255);
								strokeWeight(5);
							} else {
								stroke(0,0,255, 25);
								strokeWeight(1);
							}
							vertex((float) p4.x, (float) p4.y, (float) p4.z);
							vertex((float) p1.x, (float) p1.y, (float) p1.z);
							endShape();
						} else {
							noStroke();
							fill(0,0,255);

							Vector norm = FindNormal(i, j);
							normal((float) norm.x, (float) norm.y, (float) norm.z);

							beginShape();
							vertex((float) p1.x, (float) p1.y, (float) p1.z);
							vertex((float) p2.x, (float) p2.y, (float) p2.z);
							vertex((float) p3.x, (float) p3.y, (float) p3.z);
							vertex((float) p4.x, (float) p4.y, (float) p4.z);
							endShape(CLOSE);
						}

						popMatrix();
					}
				}

				textureWrap(REPEAT);
				//draw caps!
				if(increment < 360 && morphLoops.size() > 1 && !wireframe){
					for (int i = 0; i < morphLoops.size(); i += morphLoops.size()-1) {
						//System.out.println("size = " + morphLoops.size() + ", " + i);
						// START CAP
						PolyLoop loop = morphLoops.get(i);
						Point A = loop.points.get(0);
						Point B = loop.points.get(1);
						Point C = loop.points.get(2);

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
						for(Point p : loop.points){
							Vector AP = A.to(p);
							float u = (float)(AP.dotProduct(tan));
							float v = (float)(AP.dotProduct(binorm));
							vertex((float)p.x, (float)p.y, (float)p.z, u, v);
						}
						endShape(CLOSE);
						popMatrix();
					}
				}
			}
			popMatrix();
		}
	}

	public boolean IsSilhouette(int i_start, int j_start, int i_end, int j_end) {
		PolyLoop m_start = morphLoops.get(i_start);			// startpoint's loop
		PolyLoop m_end = morphLoops.get(i_end);				// endpoint's loop
		PolyLoop m_prev = m_start;							// prev neighboring loop
		PolyLoop m_next = m_start;							// next neighboring loop
		if ((i_start == i_end) && ((i_start == 0) || (i_start == morphLoops.size()-1))) {
			// edge of first or last face
			return true;
		} else if ((i_start != i_end) && (i_start == 0)) {
			// edge connecting to first face
			m_prev = morphLoops.get(i_start+1);
			m_next = m_prev;
		} else if ((i_start != i_end) && (i_end == 0)) {
			// edge connecting to first face
			m_prev = morphLoops.get(i_end);
			m_next = m_prev;
		} else if ((i_start != i_end) && (i_start == morphLoops.size()-1)) {
			// edge connecting to last face
			m_prev = morphLoops.get(i_start-1);
			m_next = m_prev;
		} else if ((i_start != i_end) && (i_end == morphLoops.size()-1)) {
			// edge connecting to last face
			m_prev = morphLoops.get(i_end);
			m_next = m_prev;
		} else if (morphLoops.size() < 2) {
			// only two faces, always draw them
			return true;
		} else {
			// edge has nothing to do with first or last face
			m_prev = morphLoops.get(i_start-1);		
			m_next = morphLoops.get(i_start+1);
		}

		

		/*if (i_start < morphLoops.size()-1) {		// determine if neighboring loop is before or after startpoint's loop
			m_neighbor = morphLoops.get(i_start+1);
		} else {
			m_neighbor = morphLoops.get(i_start-1);
		}*/

		Point start = m_start.points.get(j_start);	// startpoint
		Point end = m_end.points.get(j_end);		// endpoint
		Point A = start;							// prev vertex on same loop

		if (j_start <= 0) {
			A = m_start.points.get((j_start-1) + m_start.points.size());
		} else { 
			A = m_start.points.get(j_start-1);
		}
		Point B = m_start.points.get((j_start+1) % m_start.points.size());	// next vertex on same loop
		Point C = m_prev.points.get(j_start);								// same vertex on prev loop
		Point D = m_next.points.get(j_start);								// same vertex on next loop

		Vector H = new Vector(0,0,0);
		Vector N = new Vector(0,0,0);
		Vector T = (start.to(end)).normalize();
		if (end == A) {
			// endpoint is prev vertex on same loop
			//System.out.println("prev same");
			H = (start.to(C)).normalize();
			N = (start.to(D)).normalize();
		} else if (end == B) {
			// endpoint is next vertex on same loop
			//System.out.println("next same");
			H = (start.to(C)).normalize();
			N = (start.to(D)).normalize();
		} else if (end == C || end == D) {
			// endpoint is same vertex on prev loop
			//System.out.println("same diff");
			H = (start.to(A)).normalize();
			N = (start.to(B)).normalize();
		} else {
			// invalid input, assume not a silhouette
			System.out.println("invalid silhouette: " + i_start + ", " + j_start + "; " + i_end + ", " + j_end);
			return false;
		}

		Vector normal1 = T.crossProd(H);	// face 1 normal
		Vector normal2 = N.crossProd(T);	// face 2 normal

		// camera's forward vector
		Point edgePos = start.mid(end);
		Point camera = CameraPosition();
		Vector v = camera.to(edgePos);
		//Vector v = new Vector(0,0,-1d);

		return ((normal1.dotProduct(v) > 0) != (normal2.dotProduct(v) > 0));
	}

	public Point CameraPosition() {
		return new Point(eyeX, eyeY, eyeZ);
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

	public Vector FindNormal(int i1, int j1){
		PolyLoop l = morphLoops.get(i1);

		List<Vector> ns = new ArrayList<Vector>();
		Vector n1 = norms.get(i1 * l.points.size() + j1);
		//n1.draw(this, l.points.get(j1));
		ns.add(n1);


		if(mousePressed) {
			//get faces on l/r of this one
			if(((i1+1) * l.points.size() + j1) < l.points.size()) {
				Vector n2 = norms.get((i1+1) * l.points.size() + j1); 
				ns.add(n2);
			}
			if(((i1-1) * l.points.size() + j1) > 0) {
				Vector n2 = norms.get((i1-1) * l.points.size() + j1); 
				ns.add(n2);
			}

			//get face on top/bottom of this one
			if(j1+1 >= l.points.size()) { //loop around to point 0
				Vector n2 = norms.get(i1 * l.points.size()); 
				ns.add(n2);
			} else {
				Vector n2 = norms.get(i1 * l.points.size() + j1+1); 
				ns.add(n2);
			}

			if(j1-1 < 0) { //loop around to point 0
				Vector n2 = norms.get(i1 * l.points.size() + l.points.size()-1); 
				ns.add(n2);
			} else {
				Vector n2 = norms.get(i1 * l.points.size() + j1-1); 
				ns.add(n2);
			}
		}


		Vector avgNorms = n1;
		for(int k = 1; k < ns.size()-1; k++){
			Vector addV = ns.get(k);
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
