package mouse.control;

import java.util.EventListener;

public interface AngleEventListener extends EventListener{
	
	/**
	 * Call when an angle is calculated by the mouse listener
	 * @param theta in degrees*/
	public void angleReceived(double theta);

}
