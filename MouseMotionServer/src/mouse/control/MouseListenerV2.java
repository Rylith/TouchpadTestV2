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
	
	float COEF;
	int sign;
	private int dist_x = 0;
	private int dist_y = 0;
	
	//Thread for moving the mouse continuously while in bordermode//
	private int moveSpeed = 1;
	Thread movement = new Thread("movement"){
		@Override
		public void run(){
			while(borderMode){
				//Calcul y in function of the new x to stay on the straight line
				double y1=(coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
				dist_x= (int) (sign*COEF);
				dist_y= (int) (sign*(y1 - lastPointOnstraightLineY));
				mouse.motion(moveSpeed*dist_x,moveSpeed*dist_y);
			}
			//this.interrupt();
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
	
	public float onScroll(float x, float y, float distanceX, float distanceY) {
		
		current=new Point((int)x,(int)y);
		
		float intensity=0;
		
		double distance = Util.distance(center,current);
		//Log.v("BORDER", "distance: "+distance+" zone: " +(RAYON-MARGE));
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
			
			double angleOr = Math.abs(Util.angle(center,origin));
			//Log.v("BORDER MODE", "angle du courant " +angleCur+" angle de l'origine: " + angleOr);
			
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
			COEF=(float) Math.abs(angleCur-angleOr)/10;
			
			//System.out.println("Current angle: "+ angleCur);
			sign=(int) Math.signum(coefs[0]*(angleOr-180));
			
			//Calcul y in function of the new x to stay on the straight line
			double y1=(coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
			dist_x= (int) (sign*COEF);
			dist_y= (int) (sign*(y1 - lastPointOnstraightLineY));
			
			lastPointOnstraightLineX+=COEF;
			lastPointOnstraightLineY=y1;
			reglin=false;
			if (!movement.isAlive()){
				movement.start();
			}
		
			//Intensity between 0 & 1;
			if(COEF<=360){
				intensity=COEF/360;
			}else{
				intensity=1.0f;
			}
			
		} else {
			if(timerChangeMode == null || timerChangeMode.isCancelled() || timerChangeMode.isDone()){
				timerChangeMode = task.schedule(change_mode, TIMER_AFF, TimeUnit.MILLISECONDS);
			}
			dist_x= /*(int) distanceX*/0;
			dist_y= /*(int) distanceY*/0;
		}
		
		prec=current;
		//mouse.motion(dist_x, dist_y);
		return intensity;
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
