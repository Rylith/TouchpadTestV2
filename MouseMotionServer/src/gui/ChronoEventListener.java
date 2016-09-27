package gui;

import java.util.EventListener;

public interface ChronoEventListener extends EventListener {
	
	/**
	 * call when the @param chrono start*/
	public void start(Chrono chrono);
	
	/**
	 * call when @param chrono stop*/
	public void stop(Chrono chrono);

}
