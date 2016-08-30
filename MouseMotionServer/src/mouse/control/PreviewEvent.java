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
	}
	
	}
	
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
		}
	}
	
		for(PreviewEventListener lt : listeners.getListeners(PreviewEventListener.class)){
		}
	}
}
