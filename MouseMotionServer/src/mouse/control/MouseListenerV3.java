package mouse.control;

import java.awt.Point;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV3 extends IMouseListener {
	
	private Future<?> future;
	private Instant start;
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
				coefs = Util.regress(bufferY,bufferX);
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
					COEF = (float) (Math.log(start.until(Instant.now(), ChronoUnit.MILLIS)*1.0 + 1.0)/a);
				}else{
					COEF = (float) Math.log(start.until(Instant.now(), ChronoUnit.MILLIS)*1.0 + 1.0);
				}
				
				//System.out.println("coef: "+COEF);
				calculateDistanceBorderMode();
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
			dist_x= Math.round(distanceX);
			dist_y= Math.round(distanceY);
			bufferX.add(x);
			bufferY.add(y);
			lastPointOnstraightLineX=x;
			lastPointOnstraightLineY=y;
			borderMode=false;
			validPreview();
			reglin=true;
		}else if(borderMode){
			//Nothing to do, ignore the movement on screen
		}else{
			if(timerChangeMode == null || timerChangeMode.isCancelled() || timerChangeMode.isDone()){
				timerChangeMode = task.schedule(change_mode, TIMER_AFF, TimeUnit.MILLISECONDS);
			}
			dist_x= Math.round(distanceX)/*0*/;
			dist_y= Math.round(distanceY)/*0*/;
		}

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
		borderMode=false;
		if(timerChangeMode != null){
			timerChangeMode.cancel(false);
		}
		//Log.println("release");
		
	}
}
