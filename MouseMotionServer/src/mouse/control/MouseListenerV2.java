package mouse.control;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV2 implements IMouseListener{

	private Point center,origin;
	private Point current;
	private Point prec;
	
	private int RAYON;
	private int nbTour=0;
	private static final float PERCENTSCREENSIZE = 0.00f;
	
	private static double MARGE = 40;
	private List<Float> bufferX = new ArrayList<>();
    private List<Float> bufferY = new ArrayList<>();

    //To time the event on drag
    private ScheduledFuture<?> timerChangeMode = null;
    private ScheduledExecutorService task = Executors
            .newSingleThreadScheduledExecutor();
    
    private static final long TIMER_AFF = 500 ;
    private TimerTask change_mode = new TimerTask() {
        @Override
        public void run() {
            origin = current;
            borderMode = true;
        }
    };
    
    private boolean borderMode=false;
    private boolean reglin=false;
    
    private int previousSign=0;
    private int compteur=0;
    private double lastPointOnstraightLineX;
    private double lastPointOnstraightLineY;
    
    double[] coefs;
    private boolean directSens=false;
	private MouseControl mouse;
	
	private int dist_x = 0;
	private int dist_y = 0;
	
	//Thread for moving the mouse continuously while in bordermode//
	private int moveSpeed = 1;
	Thread movement = new Thread("movement"){
		@Override
		public void run(){
			while(borderMode){
				mouse.motion(moveSpeed*dist_x,moveSpeed*dist_y);
			}
			this.interrupt();
		}
	};
	
	
	public MouseListenerV2(MouseControl mouse){
		this.mouse = mouse;
	}
    
    public void setCenter(int x, int y) {
		center = new Point(x/2,y/2);
		RAYON = center.x;
        MARGE = center.x*PERCENTSCREENSIZE;
	}
	
	public void onScroll(float x, float y, float distanceX, float distanceY) {
		
		current=new Point((int)x,(int)y);
		
		double COEF;
		
		double distance = Util.distance(center,current);
		//Log.v("BORDER", "distance: "+distance+" zone: " +(RAYON-MARGE));
		if(distance < (RAYON - MARGE)){
			if(timerChangeMode != null){
				timerChangeMode.cancel(false);
			}
			if(timerChangeMode != null){
				timerChangeMode.cancel(false);
			}
			dist_x= Math.round(distanceX);
			dist_y= Math.round(distanceY);
			bufferX.add(x);
			bufferY.add(y);
			lastPointOnstraightLineX=x;
			lastPointOnstraightLineY=y;
			borderMode=false;
			reglin=true;
			COEF=1;
		}else if(borderMode){
		
			double angleCur = Math.abs(Util.angle(center,current));
			double anglePrec = Math.abs(Util.angle(center,prec));
			
			int sign = (int) Math.signum(angleCur-anglePrec);
			//Detect reversal of the direction of rotation
			if(sign != previousSign){
				previousSign=sign;
				if(previousSign != 0){
				    origin=prec;
				}
			}
			
			double angleOr = Math.abs(Util.angle(center,origin));
			//Log.v("BORDER MODE", "angle du courant " +angleCur+" angle de l'origine: " + angleOr);
			sign = (int) Math.signum(angleCur-angleOr);
			
			//Log.v("BORDER","signe: "+sign);
			//Calcul of coefficients for the straight line
			if(reglin){
				coefs = Util.regress(bufferY,bufferX);
			}
			COEF=Math.abs(angleCur-angleOr);
			//Calcul y in function of the new x to stay on the straight line
			double y1=(coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
			dist_x= (int) (sign*COEF);
			dist_y= (int) (sign*(y1 - lastPointOnstraightLineY));
			
			lastPointOnstraightLineX+=COEF;
			lastPointOnstraightLineY=y1;
			reglin=false;
			movement.start();
		
		} else {
			if(timerChangeMode == null || timerChangeMode.isCancelled() || timerChangeMode.isDone()){
				timerChangeMode = task.schedule(change_mode, TIMER_AFF, TimeUnit.MILLISECONDS);
			}
			dist_x= /*(int) distanceX*/0;
			dist_y= /*(int) distanceY*/0;
		}
		
		prec=current;
		mouse.motion(dist_x, dist_y);
	}

	public void resetBuffers(float x,float y) {
		borderMode=false;
		reglin=true;
		directSens=false;
        bufferX.clear();
        bufferY.clear();
        nbTour=0;
        //Init de the prec point before scrolling
        prec=new Point((int)x,(int)y);
		
	}

	public void press() {
		mouse.press();
	}

	public void release() {
		mouse.release();
	}

	public void click() {
		mouse.press();
		mouse.release();
	}

	public void doubleClick() {
		click();
		click();
	}

	//Part creation of a drawing window to visualize the movements
	
	
	
}
