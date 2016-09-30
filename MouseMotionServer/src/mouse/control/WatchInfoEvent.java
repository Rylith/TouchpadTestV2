package mouse.control;

import javax.swing.event.EventListenerList;

public class WatchInfoEvent {
	
	private static final EventListenerList listeners = new EventListenerList();
	
	public void addAngleEventListener(WatchInfoEventListener lt){
		listeners.add(WatchInfoEventListener.class, lt);
	}
	
	public void removeAngleEventListener(WatchInfoEventListener lt){
		listeners.remove(WatchInfoEventListener.class, lt);
	}
	
	public void sendAngle(double theta){
		for(WatchInfoEventListener lt : listeners.getListeners(WatchInfoEventListener.class)){
			lt.angleReceived(theta);
		}
	}
	
	public void sendWatchScreenSize(int width, int height){
		for(WatchInfoEventListener lt : listeners.getListeners(WatchInfoEventListener.class)){
			lt.watchScreenSizeReceived(width, height);;
		}
	}
	
	

}
