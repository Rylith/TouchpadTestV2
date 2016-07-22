package mouse.control;

import java.awt.Point;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;

public class MouseListenerV3 extends IMouseListener {
	
	private int dist_x = 0;
	private int dist_y = 0;
	private static final long TIMER_WAIT_MOVEMENT_THREAD=50;
	private Future<?> future;
	
	private Thread movement = new Thread("movement"){
		@Override
		public void run(){
			while(borderMode){
				float y1=(float) (coefs[0]*(lastPointOnstraightLineX + 1)+coefs[1]);
				dist_x= (int) sign*1;
				dist_y= (int) (sign*(y1 - lastPointOnstraightLineY));
				lastPointOnstraightLineX+=1;
				lastPointOnstraightLineY=y1;
				mouse.motion(dist_x,dist_y);
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
		}else if(borderMode){
			if(future == null || future.isDone()){
				if(reglin){
					coefs = Util.regress(bufferY,bufferX);
				}
				double angleOr = Math.abs(Util.angle(center,origin));
				sign=(int) Math.signum(coefs[0]*(angleOr-180));
				future = task.submit(movement);
				reglin=false;
			}
		}else {
			if(timerChangeMode == null || timerChangeMode.isCancelled() || timerChangeMode.isDone()){
				timerChangeMode = task.schedule(change_mode, TIMER_AFF, TimeUnit.MILLISECONDS);
			}
			dist_x= /*(int) distanceX*/0;
			dist_y= /*(int) distanceY*/0;
		}

		if (future == null || future.isDone()){
			mouse.motion(dist_x, dist_y);
		}
		return intensity;
	}
}
