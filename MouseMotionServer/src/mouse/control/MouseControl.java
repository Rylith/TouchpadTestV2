package mouse.control;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import gui.AddCursorEvent;
import java.awt.Component;
import java.awt.MouseInfo;

public class MouseControl {
	
	private Robot mouse;
	private Cursor cursor=null;
	private boolean pressed=false;
	private static Component frame;
	private static int COEF = 2;
	private static int id;
	//Subdivision includes in R+*
	private static double SUBDIVISION = 1; 
	
	//Parameters for the fluidity method
	private static int testFluidity = 6;
	private static int multiFluidity =2;
	
	public MouseControl(){
		 try {
				this.mouse = new Robot();
				if(frame !=null){
					this.cursor=initCursor(frame);
					new AddCursorEvent().fireAddCursor(cursor);
				}
			} catch (AWTException e) {
				e.printStackTrace();
			}
	}
	
	public static void setInterface(Component frame){
		MouseControl.frame=frame;
	}
	
	public void motion(int x, int y){
		Point current_cursor_point = new Point(0,0);
		if(cursor!=null){
			current_cursor_point = cursor.getPoint();
			if(!cursor.possessCursor() && pressed){
				mouse.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			}
		}else{
			current_cursor_point = MouseInfo.getPointerInfo().getLocation();
		}
		int dx = x * COEF;
		int dy = y * COEF;
		//System.out.println("Distance x: "+dx+", "+"y: "+dy);
		
		int c_x = -dx + current_cursor_point.x;
		int c_y = -dy + current_cursor_point.y;
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
			
			c_x=(int) (-i + current_cursor_point.x);
			c_y=(int) (-j + current_cursor_point.y);
			
			if(cursor!=null){
				cursor.mouseMove(c_x, c_y);
			}
			mouse.mouseMove(c_x, c_y);
			
		}
		//current_point = MouseInfo.getPointerInfo().getLocation();
		//System.out.println("Reaching Point: "+current_point.x+", "+current_point.y);
		
	}
	
	public void press(){
		if(cursor!=null){
			mouse.mouseMove(cursor.getPoint().x, cursor.getPoint().y);
		}
		mouse.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		//cursor.mousePress();
		pressed=true;
	}
	
	public void release(){
		if(cursor!=null){
			mouse.mouseMove(cursor.getPoint().x, cursor.getPoint().y);
		}
		mouse.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		//cursor.mouseRelease();
		pressed=false;
	}
	
	public static void setCoeff(int val){
		COEF = val;
	}
	
	public static int getCoeff(){
		return COEF;
	}
	
	public static void setMultiF(int val){
		multiFluidity = val;
	}
	
	public static int getMultiF(){
		return multiFluidity;
	}
	
	public static void setTestF(int val){
		testFluidity = val;
	}
	
	public static int getTestF(){
		return testFluidity;
	}

	public boolean isPressed() {
		return pressed;
	}
	
	private Cursor initCursor(Component frame){
		Cursor cursor =null;
		/*ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
	    Controller[] ca = ce.getControllers();
	    if (ca.length == 0) {
	      System.out.println("No controllers found");
	      System.exit(0);
	    }
	    
	 // collect the IDs of all the mouse controllers
	    int[] mouseIDs = new int[ca.length];
	    int mouseCount = 0;
	    System.out.println("Mouse Controllers:");
	    for (int i = 0; i < ca.length; i++) {
	      if (ca[i].getType() == Type.MOUSE) {
	        System.out.println("  ID " + i + "; \"" + ca[i].getName() +"\"");
	        mouseIDs[mouseCount++] = i;
	      }
	    }
	    
	    //Create cursor associate to the first controller 
	    int idx = mouseIDs[0];
	    System.out.println("\nInitializing mouse ID " + idx + "...");*/
	    cursor = new Cursor(frame, id++);
	    return cursor;
	}

	public void destroy() {
		if(cursor !=null){
			new AddCursorEvent().fireRemoveCursor(cursor);
		}
		
	}
}
