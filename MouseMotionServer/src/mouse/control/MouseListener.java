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

public class MouseListener implements IMouseListener{
	
	private MouseControl mouse = new MouseControl();
	private Point center,origin;
	private Point current;
	private Point prec;
	
	private int RAYON;
	private int nbTour=0;
	private static final float PERCENTSCREENSIZE = 0.20f;
	
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
    
    //private int previousSign=0;
    private float lastPointOnstraightLineX;
    private float lastPointOnstraightLineY;
    
    double[] coefs;
	private boolean directSens=false;
	
	public MouseListener(){
	}
    
    public void setCenter(int x, int y) {
		center = new Point(x/2,y/2);
		RAYON = center.x;
        MARGE = center.x*PERCENTSCREENSIZE;
	}
	
	public void onScroll(float x, float y, float distanceX, float distanceY) {

		int dist_x;
		int dist_y;
		float COEF;
		
		int xt = Math.round(x);
		int yt = Math.round(y);
		current=new Point(xt,yt);
		
		double distance = Util.distance(center,current);
		//System.out.println("distance: "+distance+" zone: " +(RAYON-MARGE));
		if(distance < (RAYON - MARGE)){
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
			//System.out.println("Angles [current, precedent]: "+angleCur+", "+anglePrec);
			
			/*int sign = (int) Math.signum(angleCur-anglePrec);
			//Detect reversal of the direction of rotation
			if(sign != previousSign){
				previousSign=sign;
				if(previousSign != 0){
				    origin=prec;
				}
			}*/
			
			
			double angleOr = Math.abs(Util.angle(center,origin));
			//sign = (int) Math.signum(angleCur-angleOr);
			
			//Log.v("BORDER","signe: "+sign);
			//Calcul of coefficients for the straight line
			if(reglin){
				coefs = Util.regress(bufferY,bufferX);
			}
			
			if((anglePrec>240 && angleCur<90) || (nbTour > 0 && directSens)){
				
				if(anglePrec>240 && angleCur<90){
					nbTour++;
					directSens =true;
					//System.out.println(nbTour);
				}
				angleCur+=(360*nbTour);
				//System.out.println("Sens direct: "+angleCur);
			}else if((anglePrec<90 && angleCur>240) || (nbTour > 0 && !directSens)){
				if(anglePrec<90 && angleCur>240){
					nbTour++;
					directSens=false;
					//System.out.println(nbTour);
				}
				//System.out.println("One tour or more: " + angleCur);
				angleCur-=(360*nbTour);	
			}
			//System.out.println("Angle original: "+angleOr+" Angle courant: "+angleCur);
			COEF=(float) Math.abs(angleCur-angleOr);
			
			//System.out.println("Current angle: "+ angleCur);
			int sign=(int) Math.signum(coefs[0]*(angleOr-180));
			
			//System.out.println(sign);
			//Calcul y in function of the new x to stay on the straight line
			float y1= (float) (coefs[0]*(lastPointOnstraightLineX + COEF/10)+coefs[1]);
			
			dist_x= Math.round(sign*COEF/10);
			dist_y= Math.round(sign*(y1 - lastPointOnstraightLineY));
			
			//System.out.println("distances : "+ dist_x+", "+dist_y);
			
			lastPointOnstraightLineX+=(COEF/10);
			lastPointOnstraightLineY=y1;
			reglin=false;
		
		}else{
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
	
	

}
