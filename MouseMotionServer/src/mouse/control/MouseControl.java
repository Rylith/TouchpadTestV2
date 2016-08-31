package mouse.control;

import java.awt.Point;
import javax.swing.JFrame;

import gui.AddCursorEvent;

public class MouseControl {
	
	//private Robot mouse;

	private Cursor cursor=null;
	private Point lastPoint ;
	private boolean pressed=false;
	private static JFrame frame;
	private static int COEF = 2;

	private static int id;

	private static boolean enablePreview = true;
	private static PreviewEvent previewEvent;

	//Subdivision includes in R+*
	private static double SUBDIVISION = 1; 
	
	//Parameters for the fluidity method
	private static int testFluidity = 6;
	private static int multiFluidity = 2;
	
	public MouseControl(){
		 /*try {
				this.mouse = new Robot();
				
			} catch (AWTException e) {
				e.printStackTrace();
			}*/
		 if(frame != null){
				this.cursor=initCursor(frame);
				new AddCursorEvent().fireAddCursor(cursor);
			}

			//lastPoint=MouseInfo.getPointerInfo().getLocation();
			lastPoint = new Point(cursor.getPoint());
	}
	
	public static void setInterface(JFrame w){
		MouseControl.frame=w;
	}
	
	public MouseControl(PreviewEvent previewEvent){
		this();
		MouseControl.previewEvent=previewEvent;
	}
	
	public void motion(int x, int y, boolean preview){
		Point current_point;
		if(!preview){
			//current_point = MouseInfo.getPointerInfo().getLocation();
			current_point = cursor.getPoint();
		}else{
			current_point=lastPoint;
		}

		int dx = x * COEF;
		int dy = y * COEF;
		//System.out.println("Distance x: "+dx+", "+"y: "+dy);
		
		int n_x = -dx + current_point.x;
		int n_y = -dy + current_point.y;
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
				//mouse.mouseMove(n_x, n_y);
				cursor.mouseMove(n_x, n_y);
			}else{
				previewEvent.setPreview(n_x, n_y, cursor);
			}
		}
		lastPoint.x=n_x;
		lastPoint.y=n_y;
		//current_point = MouseInfo.getPointerInfo().getLocation();
		//System.out.println("Reaching Point: "+current_point.x+", "+current_point.y);
		
	}
	
	public void press(){
		//mouse.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		cursor.mousePress();
		pressed=true;
	}
	
	public void release(){
		//mouse.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		cursor.mouseRelease();
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
	
	private Cursor initCursor(JFrame frame){
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
		
	public static void setEnablePreview(boolean enablePreview) {
		MouseControl.enablePreview = enablePreview;
	}
	
	public static boolean isEnablePreview(){
		return enablePreview;
	}

	public void goLastPoint() {
		if(enablePreview){
			//mouse.mouseMove(lastPoint.x, lastPoint.y);
			cursor.mouseMove(lastPoint.x, lastPoint.y);
		}
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		destroy();
		this.cursor=cursor;
	}

	public Point getLastPoint() {
		return lastPoint;
	}

	public void setLastPoint(Point lastPoint) {
		this.lastPoint = lastPoint;
	}
}
