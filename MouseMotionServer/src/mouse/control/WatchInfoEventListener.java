package mouse.control;

import java.util.EventListener;

public interface WatchInfoEventListener extends EventListener{
	
	/**
	 * Call when an angle is calculated by the mouse listener
	 * @param theta in degrees*/
	public void angleReceived(double theta);
	
	/**
	 * Call when the size of the watch screen is received
	 * @param width of the screen of watch
	 * @param height of the screen of watch*/
	public void watchScreenSizeReceived(int width,int height);

}
