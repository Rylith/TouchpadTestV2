package mouse.control;

import javax.swing.event.EventListenerList;

public class AngleEvent {
	
	private static final EventListenerList listeners = new EventListenerList();
	
	public void addAngleEventListener(AngleEventListener lt){
		listeners.add(AngleEventListener.class, lt);
	}
	
	public void removeAngleEventListener(AngleEventListener lt){
		listeners.remove(AngleEventListener.class, lt);
	}
	
	public void sendAngle(double theta){
		for(AngleEventListener lt : listeners.getListeners(AngleEventListener.class)){
			lt.angleReceived(theta);
		}
	}
	
	

}
