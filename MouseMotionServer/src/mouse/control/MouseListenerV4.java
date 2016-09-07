package mouse.control;

import java.awt.Point;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV4 extends IMouseListener {
	
	private int nbTour=0;
	
	@Override
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
				preview=false;
				COEF=1;
			}else{
				//To delay when the bordermode finish (prevent some false detection)
				if(timerExitBorderMode == null || timerExitBorderMode.isCancelled() || timerExitBorderMode.isDone()){
					timerExitBorderMode = task.schedule(exitBorderMode, TIMER_EXIT_MODE, TimeUnit.MILLISECONDS);
				}
				borderActions();	
			}
		}else{
			if(start != null){
				System.out.println("DRAG : Time between release and scroll: "+ start.until(Instant.now(),ChronoUnit.MILLIS));
			}
			System.out.println("DRAG in border mode at : "+ Instant.now().toEpochMilli());
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
			} else {
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
	
	private void borderActions() {
		double angleCur = Math.abs(Util.angle(center,current));
		double anglePrec = Math.abs(Util.angle(center,prec));
		//Log.v("BORDER MODE", "angle du courant " +angleCur+" angle de l'origine: " + angleOr);
		
		//Log.v("BORDER","signe: "+sign);
		//Calcul of coefficients for the regression line
		if(reglin){
			coefs = Util.regress(bufferY,bufferX);
			float b = (float) (mouse.getLastPoint().y - coefs[0] * mouse.getLastPoint().x);
			previewEvent.drawRegressionLine((float)coefs[0], b, isVertical);
		}
		
		anglePrec+=(360*nbTour);
		//Detect when the current angle reaches 0
		if( anglePrec>(270+(360*nbTour)) && (angleCur+(360*nbTour))<(90+(360*nbTour)) ){
			nbTour++;
			//System.out.println(nbTour);
			//System.out.println("Sens direct: "+angleCur);
		}
		
		if((anglePrec<(90+(360*nbTour)) && (angleCur+(360*nbTour))>(270+(360*nbTour)))){
			nbTour--;
			//System.out.println(nbTour);
			//System.out.println("One tour or more: " + angleCur);
				
		}
		angleCur+=(360*nbTour);
		//System.out.println("Angle original: "+angleOr+" Angle courant: "+angleCur);
		//Log.println("Precedent angle: "+anglePrec+" Angle courant: "+angleCur);
		
		//To prevent a very big increase of Y in function of the slope
		double a;
		if((a = Math.abs(coefs[0])) > 1 && !isVertical ){
			COEF= (float) (Math.abs(angleCur-anglePrec)/(DIVISION_COEF*a));
		}else{
			COEF=(float) Math.abs(angleCur-anglePrec)/DIVISION_COEF;
		}
		
		System.out.println("COEF : "+COEF);
		//System.out.println("Current angle: "+ angleCur);
		//Log.println("Current angle: "+ angleCur);
		
		//System.out.println("sign before: " + sign);
		sign = sign*(int) Math.signum(angleCur-anglePrec);
		//System.out.println("sign after: " + sign);
		calculateDistanceBorderMode();
	}

	@Override
	public void resetBuffers(float x,float y) {
		//directSens=false;
		nbTour=0;
		super.resetBuffers(x, y);
	}

}
