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
	public void setPreview(int x, int y){
		firePreviewChanged(x, y);
	}
	
	public void removePreview(){
		fireRemovePreview();
	}
	
	public void drawRegressionLine(float a, float b, boolean isVertical){
		fireDrawRegressionLine(a, b, isVertical);
	}
	
	protected void firePreviewChanged(int x, int y){
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
			lt.drawPreview(x,y);
		}
	}
	
	protected void fireRemovePreview(){
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
			lt.removePreview();
		}
	}
	
	protected void fireDrawRegressionLine(float a, float b, boolean isVertical){
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
			lt.drawRegressionLine(a, b, isVertical);;
		}
	}
}
