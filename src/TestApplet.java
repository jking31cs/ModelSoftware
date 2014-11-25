import edu.cs6491Final.*;
import processing.core.PApplet;
import processing.event.MouseEvent;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jking31 on 11/11/14.
 */
public class TestApplet extends PApplet {

    SplineLine l1;
    SplineLine l2;
    SplineLine active;
    List<BallMorphData> data = new ArrayList<>();
    boolean doBallMorph = false;

    @Override
    public void setup() {
        size(600,600,P3D);
        l1 = active = new SplineLine();
        l2 = new SplineLine();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (active.closestPointWithinRange(new Point(e.getX(), e.getY(), 0), 20) == null)
            active.addPoint(new Point(e.getX(), e.getY(), 0), 10);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point mousePoint = new Point(e.getX(), e.getY(), 0);
        SplineLine.ControlPoint pt = active.closestPointWithinRange(
            mousePoint, 100
        );
        if (pt != null) {
            if (e.isControlDown()) {
                pt.r = mousePoint.distanceTo(pt);
            } else {
                pt.x = mousePoint.x;
                pt.y = mousePoint.y;
            }
        }
    }
    
    public void keyPressed(KeyEvent e){
    	if(e.getKey()=='n')	l1.offsetMode="NORMAL";
    	if(e.getKey()=='r')	l1.offsetMode="RADIAL";
    	if(e.getKey()=='b')	l1.offsetMode="BALL";
    	if(e.getKey()==']')	l1.subDivisions++;
    	if(e.getKey()=='[')	l1.subDivisions--;
        if(e.getKey()=='1') active = l1;
        if(e.getKey()=='2') active = l2;
        if(e.getKey()=='m') {
            doBallMorph = !doBallMorph;
        }
    }

    int frameCount = 0;
    @Override
    public void draw() {
        background(255);
        l1.draw(this);
        l2.draw(this);

        if (doBallMorph) {
            Utils.ballMorphInterpolation(l1, l2, (frameCount++%180)/180d).draw(this);
        } else {
            frameCount = 0;
        }

    }

    public static void main(String[] args) {
        PApplet.main("TestApplet");
    }
}
