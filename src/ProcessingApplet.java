
import processing.core.*;
import com.cs6491final.*;

public class ProcessingApplet extends PApplet {
	

	private static final long serialVersionUID = 1L;
	
	int W_HEIGHT=768;
	int W_WIDTH=1024;
	
	
	Polyloop main;
	Polyloop mirrored;

	public void setup(){
		size(W_WIDTH, W_HEIGHT, P3D);
		main=new Polyloop(this);
		mirrored=new Polyloop(this);
	}
	
	public void draw(){
		background(255);
		main.drawPolyloop2D();
		mirrored.drawPolyloop2D();
		
	}
	
	public void mouseClicked(){
		main.addPoint(new Point(mouseX,mouseY,this));
		mirrored.addPoint(new Point(W_WIDTH-mouseX,mouseY,this));
	}
	
	public void mouseDragged(){
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
	public static void main(String[] args) {
		PApplet.main("ProcessingApplet");
	}
}
