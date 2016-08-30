package mouse.control;

import javax.swing.event.EventListenerList;

public class PreviewEvent {
	
	private static final EventListenerList listeners = new EventListenerList();
	
	public void addPreviewEventListener(PreviewEventListener listener){
		listeners.add(PreviewEventListener.class, listener);
	}
	
	public void removePreviewEventListener(PreviewEventListener listener){
		listeners.remove(PreviewEventListener.class, listener);
	}
	/**
	 * Set the last point in the preview*/
	public void setPreview(int x, int y, Cursor cursor){
		firePreviewChanged(x, y, cursor);
	}
	
	public void removePreview(Cursor cursor){
		fireRemovePreview(cursor);
	}
	
	protected void firePreviewChanged(int x, int y, Cursor cursor){
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
			lt.drawPreview(x,y,cursor);
		}
	}
	
	protected void fireRemovePreview(Cursor cursor){
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
			lt.removePreview(cursor);
		}
	}
}
