
import java.util.ArrayList;

import processing.core.*;

import com.cs6491final.*;

public class ProcessingApplet extends PApplet {
	

	private static final long serialVersionUID = 1L;
	
	int W_HEIGHT=768;
	int W_WIDTH=1024;
	String instructions="Click on the left half of the screen to add points to the polyloop.";
	String instructions2="Mirrored version show's up on the right side, vertices can be dragged independenly in both, hit P to animate from left image to right image.";
	
	
	Polyloop main;
	Polyloop mirrored;
	Polyloop unmirrored;
	Polyloop animating;
	
	boolean morphView=false;
	ArrayList<PVector> lerpVecs;

	public void setup(){
		size(W_WIDTH, W_HEIGHT, P3D);
		frameRate(60);
		main=new Polyloop(this);
		mirrored=new Polyloop(this);
		animating=new Polyloop(this);
		
		
		
	}
	
	public void draw(){
		background(255);
		
		if(!morphView){
			main.drawPolyloop2D();
			mirrored.drawPolyloop2D();
			stroke(255,0,0);
			strokeWeight(4);
			line(W_WIDTH/2,0,W_WIDTH/2,W_HEIGHT-50);
		}else{
			main.drawPolyloop2D();
			unmirrored.drawPolyloop2D();
			for(int aniIndex=0;aniIndex<main.points.size();aniIndex++){
				animating.points.get(aniIndex).x+=lerpVecs.get(aniIndex).x;
				animating.points.get(aniIndex).y+=lerpVecs.get(aniIndex).y;
			}
			animating.drawPolyloop2D();
		}
		strokeWeight(1);
		stroke(0);
		fill(0);
		drawInstructions();
	}
	
	public void mouseClicked(){
		if(!morphView){
			if(mouseX<W_WIDTH/2)
			{
				main.addPoint(new Point(mouseX,mouseY,this));
				mirrored.addPoint(new Point(W_WIDTH-mouseX,mouseY,this));
			}
		}
	}
	
	public void mouseDragged(){
		if(!morphView)
		{
			if(getDraggedPoint(mouseX,mouseY,main)!=-1)
			{
				main.points.get(getDraggedPoint(mouseX,mouseY,main)).x=mouseX;
				main.points.get(getDraggedPoint(mouseX,mouseY,main)).y=mouseY;
			}
			if(getDraggedPoint(mouseX,mouseY,mirrored)!=-1)
			{
				mirrored.points.get(getDraggedPoint(mouseX,mouseY,mirrored)).x=mouseX;
				mirrored.points.get(getDraggedPoint(mouseX,mouseY,mirrored)).y=mouseY;
			}
		}
	}
	
	public void keyReleased(){
		if(key == 'p'){
			unmirrored=new Polyloop(this);
			lerpVecs=new ArrayList<PVector>();
			for(Point p:mirrored.points){
				unmirrored.addPoint(new Point(-p.x+W_WIDTH,p.y,this));
			}
			for(int vecIndex=0;vecIndex<main.points.size();vecIndex++){
				lerpVecs.add(PVector.sub(
						new PVector((float)unmirrored.points.get(vecIndex).x, (float)unmirrored.points.get(vecIndex).y), 
						new PVector((float)main.points.get(vecIndex).x, (float)main.points.get(vecIndex).y)));
			}
			for(PVector v:lerpVecs){
				v.div(120);
			}
			animating=new Polyloop(this);
			for(Point p:main.points){
				animating.addPoint(new Point(p.x,p.y,this));
			}
			
			morphView=!morphView;
		}
	}
	
	public int getDraggedPoint(float x,float y, Polyloop poly){
		PVector mouseVector=new PVector(x,y);
		for(int ptIndex=0;ptIndex<poly.points.size();ptIndex++){
			PVector ptVector=new PVector((float)poly.points.get(ptIndex).x, (float)poly.points.get(ptIndex).y);
			float distance=PVector.sub(ptVector, mouseVector).mag();
			if(distance < 15)
				return ptIndex;
		}
		return -1;
	}
	
	public void drawInstructions(){
		text(instructions,5,W_HEIGHT-30);
		text(instructions2,5,W_HEIGHT-15);
	}
	public static void main(String[] args) {
		PApplet.main("ProcessingApplet");
	}
}
