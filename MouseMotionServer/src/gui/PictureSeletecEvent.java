package gui;

import javax.swing.event.EventListenerList;

import gui.InterfaceGame.Doc;

public class PictureSeletecEvent {
	
private static final EventListenerList listeners = new EventListenerList();
	
	public void addPictureListener(PictureSelectListener listener){
		listeners.add(PictureSelectListener.class, listener);
	}
	
	public void removePictureListener(PictureSelectListener listener){
		listeners.remove(PictureSelectListener.class, listener);
	}
	
	protected void fireSelectedPicture(Doc doc){
		for(PictureSelectListener lt : listeners.getListeners(PictureSelectListener.class)){
			lt.pictureSelected(doc);
		}
	}
	
	public void pictureSelectedEvent(Doc doc){
		fireSelectedPicture(doc);
	}

}
