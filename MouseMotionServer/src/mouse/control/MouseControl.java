package mouse.control;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;


public class MouseControl {
	
	private Robot mouse;
	private static int COEF = 2;
	private static final double SUBDIVISION = 50; 
	public MouseControl(){
		 try {
				this.mouse = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
	}
	
	public void motion(int x, int y){
		Point current_point = MouseInfo.getPointerInfo().getLocation();
		
		
		int n_x = -x * COEF + current_point.x;
		int n_y = -y * COEF + current_point.y;
		//System.out.println("Point to reach: "+ n_x + ", " + n_y);
		/*if(n_x>=OwnEngine.width || n_x<=0){
			n_y=current_point.y;
		}
		
		if(n_y>=OwnEngine.height || n_y<=0){
			n_x=current_point.x;
		}*/

		for(double i=(x*COEF)/SUBDIVISION,j=(y*COEF)/SUBDIVISION,k=0 ; k<=SUBDIVISION ; i+=((x*COEF)/SUBDIVISION),j+=((y*COEF/SUBDIVISION)),k++){
			n_x=(int) (-i + current_point.x);
			n_y=(int) (-j + current_point.y);
			//System.out.println("Subdivision: "+ n_x + ", " + n_y);
			//System.out.println("i et j: "+i+", "+j);
			mouse.mouseMove(n_x, n_y);
		}
		//current_point = MouseInfo.getPointerInfo().getLocation();
		//System.out.println("Reaching Point: "+current_point.x+", "+current_point.y);
		
	}
	
	public void press(){
		mouse.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public void release(){
		mouse.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

}
