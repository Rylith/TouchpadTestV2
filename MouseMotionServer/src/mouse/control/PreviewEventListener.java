package mouse.control;

import java.util.EventListener;

public interface PreviewEventListener extends EventListener{
	
	/**
	 * @param : x coordinate of the next position of mouse  
	 * @param : y coordinate of the next position of mouse */
	public void drawPreview(int x, int y);
	
	public void removePreview();

}