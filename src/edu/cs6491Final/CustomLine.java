package edu.cs6491Final;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobby on 11/22/14.
 */
public class CustomLine extends ArrayList<GeneratedPoint> implements Drawable {

	@Override
	public void draw(PApplet p) {
		for (int i = 1; i < this.size(); i++) {
			Point p1 = this.get(i-1);
			Point p2 = this.get(i);
			p.line((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		}
	}
}
