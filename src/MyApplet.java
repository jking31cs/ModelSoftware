import edu.cs6491Final.Point;
import edu.cs6491Final.PolyLoop;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class MyApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	boolean drawMode, viewMode;
	
	PolyLoop l1, l2;
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKey() == 'd') {
			drawMode = true;
			viewMode = false; 
		}
		if (e.getKey() == 'v') {
			viewMode = true;
			drawMode = false;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (drawMode) {
			l1.addPoint(new Point(e.getX(), e.getY(), 0));
		}
	}
	
	@Override
	public void setup() {
		size(600,600,P3D);
		drawMode = true;
		l1 = new PolyLoop();
		l2 = new PolyLoop();
		background(255);
	}
	
	@Override
	public void draw() {
		if (drawMode) {
			background(255);
			l1.draw(this);
		} else {
			//TODO add 3D morphing here.
		}
	}

	public static void main(String[] args) {
		PApplet.main("MyApplet");
	}

}
