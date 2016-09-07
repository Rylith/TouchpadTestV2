package mouse.control;

import java.awt.Point;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV2 extends IMouseListener{

    //private boolean directSens=false;
    private int nbTour=0;

	//Thread for moving the mouse continuously while in bordermode//
	private int moveSpeed = 1;
	private Future<?> future;
	private Thread movement = new Thread("movement"){
		@Override
		public void run(){
			while(borderMode){
				//Calcul y in function of the new x to stay on the straight line
					calculateDistanceBorderMode();
					mouse.motion(moveSpeed*dist_x,moveSpeed*dist_y,preview);
				try {
					sleep(TIMER_WAIT_MOVEMENT_THREAD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//Log.println("fin du thread de déplacement");
			//this.interrupt();
		}
	};
	
	public float onScroll(float x, float y, float distanceX, float distanceY) {
		
		int xt = Math.round(x);
		int yt = Math.round(y);
		current=new Point(xt,yt);
		
		float intensity=0;
		
		double distance = Util.distance(center,current);
		//System.out.println("distance: "+distance+" zone: " +(RAYON-MARGE));
		if(distance < (RAYON - MARGE)){
			if(timerChangeMode != null){
				timerChangeMode.cancel(false);
			}
			if(!borderMode){
				dist_x= Math.round(distanceX);
				dist_y= Math.round(distanceY);
				bufferX.add(x);
				bufferY.add(y);
				lastPointOnstraightLineX=x;
				lastPointOnstraightLineY=y;
				reglin=true;
				validPreview();
				COEF=1;
			}else{
				//To delay when the bordermode finish (prevent some false detection)
				if(timerExitBorderMode == null || timerExitBorderMode.isCancelled() || timerExitBorderMode.isDone()){
					timerExitBorderMode = task.schedule(exitBorderMode, TIMER_EXIT_MODE, TimeUnit.MILLISECONDS);
				}
				borderActions();	
			}
		}else{
			if(timerExitBorderMode != null && !timerExitBorderMode.isCancelled()){
				timerExitBorderMode.cancel(false);
			} 
			if(borderMode){
				borderActions();
				reglin=false;
				
				//Intensity between 0 & 1; DEPRECATED
				/*if(COEF<=(360/DIVISION_COEF)){
					intensity=COEF/(360/DIVISION_COEF);
				}else{
					intensity=1.0f;
				}*/
			}else {
				if(timerChangeMode == null || timerChangeMode.isCancelled() || timerChangeMode.isDone()){
					timerChangeMode = task.schedule(change_mode, TIMER_AFF, TimeUnit.MILLISECONDS);
				}
				dist_x= Math.round(distanceX)/*0*/;
				dist_y= Math.round(distanceY)/*0*/;
			}
		}
		prec=current;
		if (future == null || future.isDone()){
			mouse.motion(dist_x, dist_y,preview);
		}
		return intensity;
	}
	
	private void borderActions() {
		double angleCur = Math.abs(Util.angle(center,current));
		double anglePrec = Math.abs(Util.angle(center,prec));
		//Log.v("BORDER MODE", "angle du courant " +angleCur+" angle de l'origine: " + angleOr);
		
		//Log.v("BORDER","signe: "+sign);
		//Calcul of coefficients for the straight line
		if(reglin){
			coefs = Util.regress(bufferY,bufferX);
		}
		
		//Detect when the current angle reaches 0
		if((anglePrec>270 && (angleCur+(360*nbTour))<(90+(360*nbTour)))){
			nbTour++;
			//System.out.println(nbTour);
			//System.out.println("Sens direct: "+angleCur);
		}
		
		if((anglePrec<90 && (angleCur+(360*nbTour))>(270+(360*nbTour)))){
			nbTour--;
			//System.out.println(nbTour);
			//System.out.println("One tour or more: " + angleCur);
				
		}
		angleCur+=(360*nbTour);
		//System.out.println("Angle original: "+angleOr+" Angle courant: "+angleCur);

		double a;
		if((a = Math.abs(coefs[0])) > 1 && !isVertical ){
			COEF= (float) (Math.abs(angleCur-angleOr)/(DIVISION_COEF*a));
		}else{
			COEF=(float) Math.abs(angleCur-angleOr)/DIVISION_COEF;
		}

		//System.out.println("Current angle: "+ angleCur);
		
		if (future == null || future.isDone()){
			future = task.submit(movement);
		}
	}

	@Override
	public void resetBuffers(float x,float y) {
		//directSens=false;
		nbTour=0;
		super.resetBuffers(x, y);
	}
	
}
