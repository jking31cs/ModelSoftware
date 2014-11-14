import edu.cs6491Final.SplineLine;
import edu.cs6491Final.Point;
import processing.core.PApplet;
import processing.event.MouseEvent;

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
            mousePoint, 50
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

    @Override
    public void draw() {
        background(255);
        line.draw(this);
    }

    public static void main(String[] args) {
        PApplet.main("TestApplet");
    }
}
