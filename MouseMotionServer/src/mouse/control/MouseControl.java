package mouse.control;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.time.Instant;

import org.libpointing.DisplayDevice;
import org.libpointing.PointingDevice;
import org.libpointing.TransferFunction;


public class MouseControl {
	
	private Robot mouse;
	private Point lastPoint = new Point();
	private boolean pressed=false;
	private static boolean enablePreview = true;
	private static TransferFunction func;
	private static PreviewEvent previewEvent;
	//Subdivision includes in R+*
	private static double SUBDIVISION = 1;
	private boolean prevPreview = false;
	
	public MouseControl(){
		 try {
				this.mouse = new Robot();
				lastPoint=MouseInfo.getPointerInfo().getLocation();
				if(func == null){
					DisplayDevice output = new DisplayDevice("any:");
					PointingDevice input = new PointingDevice("any:");
					func = new TransferFunction("system:", input, output);
				}
			} catch (AWTException e) {
				e.printStackTrace();
			}
	}
	
	public MouseControl(PreviewEvent previewEvent){
		this();
		MouseControl.previewEvent=previewEvent;
	}
	
	public void motion(int x, int y, boolean preview){
		Point current_point;
		if(!preview){
			current_point = MouseInfo.getPointerInfo().getLocation();
		}else{
			current_point=lastPoint;
		}
		
		if(preview != prevPreview && preview){
			previewEvent.setPreview(current_point.x, current_point.y);
		}
		prevPreview=preview;
		
		
		//Apply native transfer function
		Point np = func.applyi(x, y, Instant.now().toEpochMilli());
		int dx = np.x;
		int dy = np.y;
		
		int n_x = -np.x + current_point.x;
		int n_y = -np.y + current_point.y;
		//System.out.println("Point to reach: "+ n_x + ", " + n_y);
		/*if(n_x>=OwnEngine.width || n_x<=0){
			n_y=current_point.y;
		}
		
		if(n_y>=OwnEngine.height || n_y<=0){
			n_x=current_point.x;
		}*/
		//SUBDIVISION = Util.fluidity(dx,dy,testFluidity,multiFluidity);
		//System.out.println("Subdivision: " + SUBDIVISION);
		for(double i=(dx)/SUBDIVISION,j=(dy)/SUBDIVISION,k=0 ; k<=SUBDIVISION ; i+=((dx)/SUBDIVISION),j+=((dy/SUBDIVISION)),k++){
			n_x=(int) (-i + current_point.x);
			n_y=(int) (-j + current_point.y);
			
			//System.out.println("Subdivision: "+ n_x + ", " + n_y);
			//System.out.println("i and j: "+i+", "+j);
			if(!preview || !enablePreview){
				mouse.mouseMove(n_x, n_y);
			}else{
				previewEvent.setPreview(n_x, n_y);
			}
		}
		lastPoint.x=n_x;
		lastPoint.y=n_y;
		//current_point = MouseInfo.getPointerInfo().getLocation();
		//System.out.println("Reaching Point: "+current_point.x+", "+current_point.y);
		
	}
	
	public void press(){
		mouse.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		pressed=true;
	}
	
	public void release(){
		mouse.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		pressed=false;
	}
	

	public boolean isPressed() {
		return pressed;
	}

	public static void setEnablePreview(boolean enablePreview) {
		MouseControl.enablePreview = enablePreview;
	}
	
	public static boolean isEnablePreview(){
		return enablePreview;
	}

	public void goLastPoint() {
		if(enablePreview){
			mouse.mouseMove(lastPoint.x, lastPoint.y);
		}
	}
	
	public Point getLastPoint(){
		return lastPoint;
	}

	public void moveTo(int x, int y, boolean preview) {
		if(!preview || !enablePreview){
			mouse.mouseMove(x, y);
		}else{
			previewEvent.setPreview(x, y);
		}
		lastPoint.x=x;
		lastPoint.y=y;
	}
}
