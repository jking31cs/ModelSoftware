import edu.cs6491Final.SplineLine;
import edu.cs6491Final.Point;
import processing.core.PApplet;
import processing.event.MouseEvent;
import processing.event.KeyEvent;

/**
 * Created by jking31 on 11/11/14.
 */
public class TestApplet extends PApplet {

    SplineLine line;

    @Override
    public void setup() {
        size(600,600,P3D);
        line = new SplineLine();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (line.closestPointWithinRange(new Point(e.getX(), e.getY(), 0), 20) == null)
            line.addPoint(new Point(e.getX(), e.getY(), 0), 10);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point mousePoint = new Point(e.getX(), e.getY(), 0);
        SplineLine.ControlPoint pt = line.closestPointWithinRange(
            mousePoint, 100
        );
        if (pt != null) {
            if (e.isControlDown()) {
                pt.r = mousePoint.distanceTo(pt.pt);
            } else {
                pt.pt.x = mousePoint.x;
                pt.pt.y = mousePoint.y;
            }
        }
    }
    
    public void keyPressed(KeyEvent e){
    	if(e.getKey()=='n')	line.offsetMode="NORMAL";
    	if(e.getKey()=='r')	line.offsetMode="RADIAL";
    	if(e.getKey()=='b')	line.offsetMode="BALL";
    	if(e.getKey()==']')	line.subDivisions++;
    	if(e.getKey()=='[')	line.subDivisions--;
    	if(e.getKey()=='f')	line.fillCurve=!line.fillCurve;
    }

    @Override
    public void draw() {
        background(255);
        line.draw(this);
        if(line.pts.size()>2)
        	line.getBoundingLoop().draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("TestApplet");
    }
}
