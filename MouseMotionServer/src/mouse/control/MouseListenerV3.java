package mouse.control;

import java.awt.Point;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import gui.Log;
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
				COEF = (float) Math.log(start.until(Instant.now(), ChronoUnit.MILLIS)*1.0 + 1.0);
				//System.out.println("coef: "+coef);
				//float intensity = COEF/20;
				//System.out.println(intensity);
				float y1=(float) (coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
				dist_x= (int) (sign*COEF);
				dist_y= Math.round(sign*(y1 - lastPointOnstraightLineY));
				lastPointOnstraightLineX+=COEF;
				lastPointOnstraightLineY=y1;
				//channel.send(("VIBRATION,"+intensity).getBytes(), 0, ("VIBRATION,"+intensity).getBytes().length);
				//key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void release() {
		//mouse.release();
		borderMode=false;
		movement.stop();
		if(timerChangeMode != null){
			timerChangeMode.cancel(false);
		}
		Log.println("release");
		
	}
}
