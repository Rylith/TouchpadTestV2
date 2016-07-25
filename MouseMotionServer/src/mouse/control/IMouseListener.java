package mouse.control;

import java.awt.Point;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import network.Impl.Util;
import network.Interface.Channel;

public abstract class IMouseListener {
	
	protected MouseControl mouse = new MouseControl();
	protected Point center,origin;
	protected Point current;
	protected Point prec;
	
	protected int RAYON;
	protected static double MARGE = 0;
	protected static float PERCENTSCREENSIZE = 0.20f;
	
	protected int dist_x = 0;
	protected int dist_y = 0;
	
    protected List<Float> bufferX = new ArrayList<>();
    protected List<Float> bufferY = new ArrayList<>();
    
    protected float COEF;
    protected static float DIVISION_COEF=10;//
    
  //To time the event on drag
    protected ScheduledFuture<?> timerChangeMode = null;
    protected ScheduledExecutorService task = Executors
            .newSingleThreadScheduledExecutor();
    
    protected static long TIMER_AFF = 500;
    protected static long TIMER_WAIT_MOVEMENT_THREAD=50;
    protected TimerTask change_mode = new TimerTask() {
        @Override
        public void run() {
            origin = current;
            borderMode = true;
        }
    };
    
    protected ScheduledFuture<?> timerExitBorderMode;
    protected TimerTask exitBorderMode = new TimerTask() {
        @Override
        public void run() {
            borderMode = false;
        }
    };
    
    protected boolean borderMode=false;
    protected boolean reglin=false;
    
    protected float lastPointOnstraightLineX;
    protected float lastPointOnstraightLineY;
    
    protected double[] coefs;
    protected int sign;
	protected Channel channel;
	protected SelectionKey key;
	
	public static void setDIVISION_COEF(float dIVISION_COEF) {
		DIVISION_COEF = dIVISION_COEF;
	}

	public static float getDIVISION_COEF() {
		return DIVISION_COEF;
	}

	/**Initialize the coordinates of the center of the device*/
	public void setCenter(int x, int y) {
		center = new Point(x/2,y/2);
		RAYON = center.x;
        MARGE = center.x*PERCENTSCREENSIZE;
	}
	
	/**Called when a continues movement is done on the screen of the device*/
	public abstract float onScroll(float x, float y, float distanceX, float distanceY);
	
	/**Reset data to calculate the line*/
	public void resetBuffers(float x,float y) {
		borderMode=false;
		reglin=true;
        bufferX.clear();
        bufferY.clear();
        //Init de the prec point before scrolling
        prec=new Point(Math.round(x),Math.round(y));
	}
	
	/**Simulate a continue left click*/
	public void press() {
		mouse.press();
	}
	
	/**Simulate the release of left click*/
	public void release() {
		mouse.release();
		borderMode=false;
	}
	
	/**Simulation of a click (press then release)*/
	public void click() {
		mouse.press();
		mouse.release();
	}
	
	/**Simulation of double click*/
	public void doubleClick() {
		click();
		click();
	}
	
	public MouseControl getMC() {
		return this.mouse;
	}

	protected void signDetermination(){
		double angleOr = Math.abs(Util.angle(center,origin));
		//System.out.println("angle origin: "+angleOr);
		if(angleOr>=340 && coefs[0]>0 || angleOr<=10 && coefs[0]<0 || angleOr<180 && angleOr>=170 && coefs[0]>0 || angleOr>180 && angleOr<=190 && coefs[0]<0){
			sign=(int) -Math.signum(coefs[0]*(angleOr-180));
		}else{
			sign=(int) Math.signum(coefs[0]*(angleOr-180));
		}
		if(sign == 0 && coefs[0] != 0){
			sign=1;
		}else if(coefs[0] == 0){
			if(angleOr >= 180 && angleOr<=190 || angleOr <= 180 && angleOr>=170){
				sign=1;
			}
			if(angleOr >= 340 || angleOr <=10 ){
				sign=-1;
			}
		}
		//System.out.println(sign);
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
