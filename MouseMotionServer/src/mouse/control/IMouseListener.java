package mouse.control;

import java.awt.Point;
import java.nio.channels.SelectionKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import network.Impl.Util;
import network.Interface.Channel;

public abstract class IMouseListener {
	
	protected Instant start;
	
	protected static PreviewEvent previewEvent = new PreviewEvent();
	protected MouseControl mouse = new MouseControl(previewEvent);
	
	protected Point center,origin;
	protected Point current;
	protected Point prec;
	protected double angleOr;
	
	protected int RAYON;
	protected static double MARGE = 0;
	protected static float PERCENTSCREENSIZE = 0.25f;
	
	protected int dist_x = 0;
	protected int dist_y = 0;
	
    protected List<Float> bufferX = new ArrayList<>();
    protected List<Float> bufferY = new ArrayList<>();
    
    protected float COEF;
    protected static float DIVISION_COEF=10;//
    
  //To time the event on drag
    protected ScheduledFuture<?> timerChangeMode = null;
    protected ScheduledExecutorService task = Executors
            .newScheduledThreadPool(2);
    
    protected static long TIMER_AFF = 500;
    protected static long TIMER_WAIT_MOVEMENT_THREAD=50;
    protected TimerTask change_mode = new TimerTask() {
        @Override
        public void run() {
        	sendFeedBack();
        	borderMode = true;
            preview = true;
            origin = current;
            angleOr = Math.abs(Util.angle(center,origin));
            isVertical = angleOr > 80 && angleOr < 100 || angleOr< 280 && angleOr>260;
            //Log.println("changement de mode: "+borderMode);
        }
    };
    
    protected ScheduledFuture<?> timerExitBorderMode;
    protected static long TIMER_EXIT_MODE = 160;
    protected TimerTask exitBorderMode = new TimerTask() {
        @Override
        public void run() {
            borderMode = false;
            sign = 0;
            System.out.println("EXIT BORDER MODE at : "+ Instant.now().toEpochMilli());
            //Log.println("Appel au thread de changement de mode: "+ borderMode);
            sendFeedBack();
        }
    };
    
    protected boolean borderMode=false;
    protected boolean preview=false;
    protected boolean reglin=false;
    protected boolean isVertical=false;
    
    protected float lastPointOnstraightLineX;
    protected float lastPointOnstraightLineY;
    
    protected float epsX=0;
	protected float epsY=0;
    
    protected double[] coefs;
    protected int sign;
	protected Channel channel;
	protected SelectionKey key;
	
	public static void setDIVISION_COEF(float DIVISION_COEF) {
		IMouseListener.DIVISION_COEF = DIVISION_COEF;
	}

	public static float getDIVISION_COEF() {
		return DIVISION_COEF;
	}

	/**Initialize the coordinates of the center of the device*/
	public void setCenter(int x, int y) {
		center = new Point(x/2,y/2);
		RAYON = center.x;
        MARGE = RAYON*PERCENTSCREENSIZE;
	}
	
	/**Called when a continues movement is done on the screen of the device*/
	public abstract float onScroll(float x, float y, float distanceX, float distanceY);
	
	/**Reset data to calculate the line*/
	public void resetBuffers(float x,float y) {
		//borderMode=false;
		if(!borderMode){
			reglin=true;
        	bufferX.clear();
        	bufferY.clear();
        	epsX=0;
        	epsY=0;
        //Init de the prec point before scrolling
        	prec=new Point(Math.round(x),Math.round(y));
        }else{
        	if(start != null)
        		System.out.println("Time between release and down: "+start.until(Instant.now(),ChronoUnit.MILLIS));
        	System.out.println("DOWN in border mode at : " + Instant.now().toEpochMilli());
        }
	}
	
	/**Simulate a continue left click*/
	public void press() {
		mouse.press();
	}
	
	/**Simulate the release of left click*/
	public void release() {
		//mouse.release();
		if(borderMode && (timerExitBorderMode == null || timerExitBorderMode.isCancelled() || timerExitBorderMode.isDone())){
			timerExitBorderMode = task.schedule(exitBorderMode, TIMER_EXIT_MODE, TimeUnit.MILLISECONDS);
			start = Instant.now();
			//Log.println("release in border mode");
			System.out.println("RELEASE in border mode at : " + start.toEpochMilli());
		}
		if(timerChangeMode != null){
			timerChangeMode.cancel(false);
		}
		//Log.println("release");
		//borderMode=false;
	}
	
	/**Simulation of a click (press then release)*/
	public void click() {
		mouse.press();
		mouse.release();
	}
	
	/**Simulation of double click*/
	public void doubleClick() {
		sendFeedBack();
		//click();
		//click();
    	unvalidPreview();
    	/*
		if(mouse.isPressed()){
			mouse.release();
		}else{
			press();
		}*/
	}
	
	protected void validPreview(){
		if(preview){
    		previewEvent.removePreview();
    		mouse.goLastPoint();
    		preview=false;
    	}
	}
	
	protected void unvalidPreview(){
		if(preview){
			previewEvent.removePreview();
			preview=false;
		}
	}

	protected void signDetermination(){
		
		//System.out.println("angle origin: "+angleOr);
		if(angleOr>=340 && coefs[0]>0 || angleOr<=10 && coefs[0]<0 || angleOr<180 && angleOr>=170 && coefs[0]>0 || angleOr>180 && angleOr<=190 && coefs[0]<0){
			sign=(int) -Math.signum(coefs[0]*(angleOr-180));
		}else{
			sign=(int) Math.signum(coefs[0]*(angleOr-180));
		}
		if(angleOr > 80 && angleOr < 100 || angleOr< 280 && angleOr>260){
			sign = (int) Math.signum(angleOr-180);
		}
		if(sign == 0 && coefs[0] != 0){
			sign=1;
		}else if(coefs[0] == 0){
			if(angleOr<=190 && angleOr>=170){
				sign=1;
			}
			if(angleOr >= 340 || angleOr <=10 ){
				sign=-1;
			}
		}
		//System.out.println(sign);
	}
	
	protected void sendFeedBack(){
		float rand =0.9f+(new Random().nextFloat()/10.0f);
        channel.send(("VIBRATION,"+rand).getBytes(), 0, ("VIBRATION,"+rand).getBytes().length);
    	key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    	key.selector().wakeup();
	}
	
	protected void calculateDistanceBorderMode(){
		if(sign == 0){
			signDetermination();
		}
		epsX = sign * epsX;
		epsY = sign * epsY;
		
		if(!isVertical){
			float y1= (float) (coefs[0]*(lastPointOnstraightLineX + COEF)+coefs[1]);
			dist_x= Math.round(sign*(COEF+epsX));
			dist_y= Math.round(sign*((y1 - lastPointOnstraightLineY)+epsY));
			
			epsX = sign*(COEF + epsX) - dist_x;
			epsY = sign*((y1 - lastPointOnstraightLineY)+epsY) - dist_y;
			
			//System.out.println("distances : "+ dist_x+", "+dist_y);
			//System.out.println("COEF : "+COEF);
			//System.out.println("Ecart en x: " + epsX);
			//System.out.println("Ecart en y: " + epsY);
			
			lastPointOnstraightLineX+=(COEF);
			lastPointOnstraightLineY=y1;
		}else{
			dist_x= 0;
			dist_y = Math.round(sign*(COEF));
		}
	}

	public void setChannel(Channel channel) {
		this.channel=channel;
	}

	public void setKey(SelectionKey key) {
		this.key=key;
	}
	
	public static void setPercentScreenSize(float val){
		PERCENTSCREENSIZE = val;
	}
	
	public static float getPercentScreenSize(){
		return PERCENTSCREENSIZE;
	}
	
	public static void setDivisionCoeff(float val){
		DIVISION_COEF = val;
	}
	
	public static float getDivisionCoeff(){
		return DIVISION_COEF;
	}
	
	public static void setTimerAff(long val){
		TIMER_AFF = val;
	}
	
	public static long getTimerAff(){
		return TIMER_AFF;
	}
	
	public static void setTimerMovement(long val){
		TIMER_WAIT_MOVEMENT_THREAD = val;
	}
	
	public static long getTimerMovement(){
		return TIMER_WAIT_MOVEMENT_THREAD;
	}
}
