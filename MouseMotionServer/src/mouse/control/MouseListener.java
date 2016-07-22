package mouse.control;

import java.awt.Point;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListener extends IMouseListener{
	
	private int nbTour=0;
    //private int previousSign=0;
    private boolean directSens=false;
	
	public float onScroll(float x, float y, float distanceX, float distanceY) {

		int dist_x;
		int dist_y;
		
		float COEF;
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
			COEF=(float) Math.abs(angleCur-angleOr)/10;
			
			//System.out.println("Current angle: "+ angleCur);
			sign=(int) Math.signum(coefs[0]*(angleOr-180));
			
			//System.out.println(sign);
			//Calcul y in function of the new x to stay on the straight line
			float y1= (float) (coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
			
			dist_x= Math.round(sign*COEF);
			dist_y= Math.round(sign*(y1 - lastPointOnstraightLineY));
			
			//System.out.println("distances : "+ dist_x+", "+dist_y);
			
			lastPointOnstraightLineX+=(COEF);
			lastPointOnstraightLineY=y1;
			reglin=false;
			
			//Intensity between 0 & 1;
			if(COEF<=36){
				intensity=COEF/36;
			}else{
				intensity=1.0f;
			}
		
		}else{
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
		directSens=false;
		nbTour=0;
		super.resetBuffers(x, y);
	}
}
