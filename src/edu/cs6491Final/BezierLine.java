package edu.cs6491Final;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jking31 on 11/13/14.
 */
public class BezierLine implements Drawable {

    public static class ControlPoint implements Drawable {

        public double r;
        public Point pt;

        public ControlPoint(double r, Point p) {
            this.r = r;
            this.pt = p;
        }

        @Override
        public void draw(PApplet p) {
            p.stroke(0,255,0);
            p.fill(0, 255, 0);
            p.ellipse((float) pt.x, (float) pt.y, (float) r, (float) r);
        }
    }

    private static class GeneratedPoint extends ControlPoint {
        public GeneratedPoint(double r, Point p) {
            super(r, p);
        }
    }

    public List<ControlPoint> pts;

    public BezierLine() {
        this.pts = new ArrayList<>();
    }

    public void addPoint(Point p, double r) {
        this.pts.add(new ControlPoint(r, p));
    }

    public List<GeneratedPoint> calculateCurve() {
        double t = 0;
        List<GeneratedPoint> toRet = new ArrayList<>();
        while (t <= 1) {
            Point p = new Point(0,0,0);
            double r = 0;
            for (int i = 0; i < pts.size(); i++) {
                Vector v = pts.get(i).pt.asVec();
                //p is now ((n choose i)*(1-t)^(n-i)*t^i)p_i
                v = v.mul(Math.pow(1-t, pts.size()-i-1)).mul(Math.pow(t, i)).mul(choose(pts.size()-1, i));
                p = p.add(v);
                r += Math.pow(1-t, pts.size()-i-1) * Math.pow(t, i) * choose(pts.size()-1, i) * pts.get(i).r;

            }
            toRet.add(new GeneratedPoint(r,p));
            t += .01;
        }
        return toRet;
    }

    public ControlPoint closestPointWithinRange(Point p, double distance) {
        double minDistance = distance;
        ControlPoint selected = null;
        for (ControlPoint pt : pts) {
            if (pt.pt.distanceTo(p) < minDistance) {
                minDistance = pt.pt.distanceTo(p);
                selected = pt;
            }
        }
        return selected;
    }

    /**
     * Returns n choose k
     */
    public static double choose(int n, int k) {
        int nCk = 1;
        for (int i = 0; i < k; i++) {
            nCk = nCk * (n-i) / (i+1);
        }
        return nCk;
    }

    @Override
    public void draw(PApplet p) {
        for (ControlPoint pt : pts) pt.draw(p);



        if (pts.size() > 1) {
            List<GeneratedPoint> generatedPoints = calculateCurve();
            for (int i = 1; i < generatedPoints.size(); i++) {
                Point prev = generatedPoints.get(i-1).pt;
                Point cur = generatedPoints.get(i).pt;
                p.stroke(255, 0, 0);
                p.strokeWeight((float) generatedPoints.get(i-1).r);
                p.line(
                    (float) prev.x,(float) prev.y,(float) prev.z,
                    (float) cur.x, (float) cur.y, (float) cur.z
                );
            }
        }

    }
}
