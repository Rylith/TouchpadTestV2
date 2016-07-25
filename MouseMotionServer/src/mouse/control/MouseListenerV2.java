package mouse.control;

import java.awt.Point;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV2 extends IMouseListener{

    private static final long TIMER_WAIT_MOVEMENT_THREAD=50;
    private boolean directSens=false;
    private int nbTour=0;
	
	float COEF;
	private int DiviCoef=10;
	private int dist_x = 0;
	private int dist_y = 0;
	
	//Thread for moving the mouse continuously while in bordermode//
	private int moveSpeed = 1;
	private Future<?> future;
	private Thread movement = new Thread("movement"){
		@Override
		public void run(){
			while(borderMode){
				//Calcul y in function of the new x to stay on the straight line
					float y1=(float) (coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
					dist_x= (int) (sign*COEF);
					dist_y= (int) (sign*(y1 - lastPointOnstraightLineY));
					lastPointOnstraightLineX+=COEF;
					lastPointOnstraightLineY=y1;
					mouse.motion(moveSpeed*dist_x,moveSpeed*dist_y);
				try {
					sleep(TIMER_WAIT_MOVEMENT_THREAD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//this.interrupt();
		}
	};
	
	public float onScroll(float x, float y, float distanceX, float distanceY) {
		
		current=new Point((int)x,(int)y);
		
		float intensity=0;
		
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
			double angleOr = Math.abs(Util.angle(center,origin));
			//Log.v("BORDER MODE", "angle du courant " +angleCur+" angle de l'origine: " + angleOr);
			
			//Log.v("BORDER","signe: "+sign);
			//Calcul of coefficients for the straight line
			if(reglin){
				coefs = Util.regress(bufferY,bufferX);
			}
			
			//Detect when the current angle reaches 0
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
			COEF=(float) Math.abs(angleCur-angleOr)/DiviCoef;

			//System.out.println("Current angle: "+ angleCur);
			sign=(int) Math.signum(coefs[0]*(angleOr-180));
			
			if (future == null || future.isDone()){
				future = task.submit(movement);
			}
			reglin=false;
			
			//Intensity between 0 & 1;
			if(COEF<=36){
				intensity=COEF/36;
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
		if (future == null || future.isDone()){
			mouse.motion(dist_x, dist_y);
		}
		return intensity;
	}
	
	@Override
	public void resetBuffers(float x,float y) {
		directSens=false;
		nbTour=0;
		super.resetBuffers(x, y);
	}
	
}
