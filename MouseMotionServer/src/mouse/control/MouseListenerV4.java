package mouse.control;

import java.awt.Point;
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
			//double angleOr = Math.abs(Util.angle(center,origin));
			//Log.v("BORDER MODE", "angle du courant " +angleCur+" angle de l'origine: " + angleOr);
			
			//Log.v("BORDER","signe: "+sign);
			//Calcul of coefficients for the straight line
			if(reglin){
				coefs = Util.regress(bufferY,bufferX);
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
			COEF=(float) Math.abs(angleCur-anglePrec)/DIVISION_COEF;

			//System.out.println("Current angle: "+ angleCur);
			//Log.println("Current angle: "+ angleCur);
			signDetermination();
			
			float y1= (float) (coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
			dist_x= Math.round(sign*COEF);
			dist_y= Math.round(sign*(y1 - lastPointOnstraightLineY));
			
			//System.out.println("distances : "+ dist_x+", "+dist_y);
			
			lastPointOnstraightLineX+=(COEF);
			lastPointOnstraightLineY=y1;
			
			reglin=false;
			
			//Intensity between 0 & 1;
			if(COEF<=(360/DIVISION_COEF)){
				intensity=COEF/(360/DIVISION_COEF);
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
		mouse.motion(dist_x, dist_y);
		return intensity;
	}
	
	@Override
	public void resetBuffers(float x,float y) {
		//directSens=false;
		nbTour=0;
		super.resetBuffers(x, y);
	}

}
