package mouse.control;

import java.awt.Point;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV1 extends IMouseListener{
	
	private int nbTour=0;
    //private int previousSign=0;
   // private boolean directSens=false;
    //private boolean directSens=false;
	
	public float onScroll(float x, float y, float distanceX, float distanceY) {
		
		float intensity=0;
		
		int xt = Math.round(x);
		int yt = Math.round(y);
		current=new Point(xt,yt);
		
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
				reglin=true;
				validPreview();
				COEF=1;
			}else{
				//To delay when the bordermode finish (prevent some false detection)
				if(moveOutBorderArea >= 5){
					borderMode = false;
		            sign = 0;
		            sendFeedBack();
		            moveOutBorderArea=0;
				}else{
					moveOutBorderArea++;
					//borderActions();
				}	
			}
		}else{
			//The movements outside the area must be contigus to exit the mode
			moveOutBorderArea=0;
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
			}else{
				if(timerChangeMode == null || timerChangeMode.isCancelled() || timerChangeMode.isDone()){
					timerChangeMode = task.schedule(change_mode, TIMER_AFF, TimeUnit.MILLISECONDS);
				}
				dist_x= Math.round(distanceX)/*0*/;
				dist_y= Math.round(distanceY)/*0*/;
			}
		}
		prec=current;
		mouse.motion(dist_x, dist_y,preview);
		
		return intensity;
	}

	@Override
	public void resetBuffers(float x,float y) {
		//directSens=false;
		nbTour=0;
		super.resetBuffers(x, y);
	}
	
	private void borderActions(){
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
		
		//Calcul of coefficients for the straight line
		calcRegLine();
		
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
		
		//Calcul y in function of the new x to stay on the straight line
		calculateDistanceBorderMode();
	}
}
