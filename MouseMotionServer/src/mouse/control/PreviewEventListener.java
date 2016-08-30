package mouse.control;

import java.util.EventListener;

public interface PreviewEventListener extends EventListener{
	
	/**
	 * @param : x coordinate of the next position of mouse  
	 * @param : y coordinate of the next position of mouse 
	 * @param : cursor could be useful for multiple cursor*/
	public void drawPreview(int x, int y, Cursor cursor);
	
	public void removePreview(Cursor cursor);

}
