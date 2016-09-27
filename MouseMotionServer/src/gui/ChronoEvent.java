package gui;

import javax.swing.event.EventListenerList;

public class ChronoEvent {
	
	private static final EventListenerList listeners = new EventListenerList();
		
	public void addChronoEventListener(ChronoEventListener listener){
		listeners.add(ChronoEventListener.class, listener);
	}
	
	public void removeChronoEventListener(ChronoEventListener listener){
		listeners.remove(ChronoEventListener.class, listener);
	}
	
	public void start(Chrono chrono){
		for(ChronoEventListener lt : listeners.getListeners(ChronoEventListener.class)){
			lt.start(chrono);
		}
	}
	
	public void stop(Chrono chrono){
		for(ChronoEventListener lt : listeners.getListeners(ChronoEventListener.class)){
			lt.stop(chrono);
		}
	}
}
