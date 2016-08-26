package gui;

import java.util.EventListener;
import javax.swing.event.EventListenerList;

import mouse.control.Cursor;

interface AddCursorEventListener extends EventListener{
	public void addCursor(Cursor cursor);
	public void removeCursor(Cursor cursor);
}

public class AddCursorEvent {
	private static final EventListenerList listeners = new EventListenerList();
	
	public void fireAddCursor(Cursor cursor){
		for(AddCursorEventListener lst : listeners.getListeners(AddCursorEventListener.class)){
			lst.addCursor(cursor);
		}
	}
	
	public void fireRemoveCursor(Cursor cursor){
		for(AddCursorEventListener lst : listeners.getListeners(AddCursorEventListener.class)){
			lst.removeCursor(cursor);
		}
	}
	
	public void addCursorEventListener(AddCursorEventListener toAdd){
		listeners.add(AddCursorEventListener.class,toAdd);
	}
	
	public void removeCursorEventListener(AddCursorEventListener listener){
		listeners.remove(AddCursorEventListener.class,listener);
	}

}
