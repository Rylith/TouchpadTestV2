package mouse.control;

import java.awt.MouseInfo;
import java.awt.Point;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV5 extends IMouseListener {

	private Future<?> future;
	private Instant start;
	private final double log2 = Math.log(2);
	private int nbTour=0;
	
	private TimerTask change_mode = new TimerTask() {
        @Override
        public void run() {
        	sendFeedBack();
            origin = current;
            borderMode = true;
            preview=true;
            angleOr = Math.abs(Util.angle(center,origin));
            isVertical = angleOr > 80 && angleOr < 100 || angleOr< 280 && angleOr>260;
            if(reglin){
            	//coefs = Util.regress(bufferY,bufferX);
    			coefs = Util.resistantLine(bufferY, bufferX);
				float b = (float) (mouse.getLastPoint().y - coefs[0] * mouse.getLastPoint().x);
				coefs[1]=b;
				lastPointOnstraightLineX = mouse.getLastPoint().x;
				lastPointOnstraightLineY = (float) (coefs[0]*lastPointOnstraightLineX +coefs[1]);
				previewEvent.drawRegressionLine((float)coefs[0], b, isVertical);
			}
			signDetermination();
			start=Instant.now();
			future = task.submit(movement);
			reglin=false;
        }
    };
	
	
	private Thread movement = new Thread("movement"){
		@Override
		public void run(){
			while(borderMode){
				//Logarithmic increase in function of time
				double a;
				if((a = Math.abs(coefs[0])) > 1 && !isVertical ){
					COEF = (float) (Math.log1p(start.until(Instant.now(), ChronoUnit.MILLIS)*1.0)/(a*log2));
				}else{
					COEF = (float) (Math.log(start.until(Instant.now(), ChronoUnit.MILLIS)*1.0 + 1.0)/log2);
				}
				
				//System.out.println("coef: "+COEF);
				synchronized (coefs) {
					calculateDistanceBorderMode();
				}
				mouse.motion(dist_x,dist_y,preview);
				try {
					sleep(TIMER_WAIT_MOVEMENT_THREAD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	@Override
	public float onScroll(float x, float y, float distanceX, float distanceY) {
		
		int xt = Math.round(x);
		int yt = Math.round(y);
		current=new Point(xt,yt);
		
		float intensity=0;
		
		x=x-center.x;
		y=y-center.y;
		
		double distance = Util.distance(center,current);
		//Log.v("BORDER", "distance: "+distance+" zone: " +(RAYON-MARGE));
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
			}else{
				//To delay when the bordermode finish (prevent some false detection)
				if(moveOutBorderArea >= 5){
					borderMode = false;
		            sign = 0;
		            sendFeedBack();
		            moveOutBorderArea=0;
				}else{
					moveOutBorderArea++;
					borderActions();
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
			}else{
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
	
	@Override
	public void release() {
		//mouse.release();
		if(borderMode){
			sendFeedBack();
		}
		nbTour=0;
		borderMode=false;
		if(timerChangeMode != null){
			timerChangeMode.cancel(false);
		}
		//Log.println("release");
		
	}
	
	private void borderActions(){
		double angleCur = Math.abs(Util.angle(center,current));
		double anglePrec = Math.abs(Util.angle(center,prec));
		
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
		
		float deviation = (float) (angleCur-anglePrec)/(DIVISION_COEF*10);
		synchronized (coefs) {
			epsX=0;
			epsY=0;
			coefs[0]+=deviation;
			//lastPointOnstraightLineY = (float) (coefs[0]*lastPointOnstraightLineX +coefs[1]);
			float b = (float) (MouseInfo.getPointerInfo().getLocation().y - coefs[0] * MouseInfo.getPointerInfo().getLocation().x);
			coefs[1]=b;
			float y = (float) (coefs[0]*mouse.getLastPoint().x +coefs[1]);
			mouse.moveTo(mouse.getLastPoint().x, Math.round(y),preview);
			lastPointOnstraightLineY=y;
			lastPointOnstraightLineX = mouse.getLastPoint().x;
			previewEvent.drawRegressionLine((float)coefs[0], b, isVertical);
		}
	}
}
